package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOPatientInfoRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatient;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpSamplesRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mskcc.domain.patient.CRDBPatientInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class CachingDMPFileEntryToSampleConverter extends BamAwareDmpFileEntryToSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(CachingDMPFileEntryToSampleConverter.class);

    private DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;
    private DmpSamplesRetriever dmpSamplesRetriever;
    private CMOPatientInfoRetriever cmoPatientIdRetriever;

    @Autowired
    public CachingDMPFileEntryToSampleConverter(DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository,
                                                DmpSamplesRetriever dmpSamplesRetriever,
                                                CMOPatientInfoRetriever cmoPatientIdRetriever,
                                                BamPathRetriever bamPathRetriever,
                                                @Qualifier("fileExistsPredicate") Predicate<String>
                                                            fileExistsPredicate) {
        super(bamPathRetriever, fileExistsPredicate);
        this.dmpPatientId2CMOPatientIdRepository = dmpPatientId2CMOPatientIdRepository;
        this.dmpSamplesRetriever = dmpSamplesRetriever;
        this.cmoPatientIdRetriever = cmoPatientIdRetriever;
    }

    @Override
    public DMPSample convertPart(DmpFileEntry dmpFileEntry, String dmpPatientId) {
        DmpPatient dmpPatient = dmpSamplesRetriever.retrieve(dmpPatientId);

        Optional<DMPSample> optionalDmpSample = dmpPatient.getSamples().values().stream()
                .filter(s -> Objects.equals(s.getDmpId(), dmpFileEntry.getDmpSampleId()))
                .findFirst();

        if (!optionalDmpSample.isPresent()) {
            throw new DMPSampleNotFoundException(String.format("DMP " +
                    "Sample with id: %s was not found in DMP repository.", dmpFileEntry.getDmpSampleId()));
        }

        DMPSample dmpSample = optionalDmpSample.get();
        String mrn = dmpPatient.getMrn();
        CRDBPatientInfo crdbPatientInfo = cmoPatientIdRetriever.resolve(mrn);

        dmpSample.setGender(DMPSample.Gender.fromString(crdbPatientInfo.getGender()));

        dmpPatientId2CMOPatientIdRepository.put(dmpPatientId, crdbPatientInfo.getPatientId());

        LOGGER.info(String.format("Patient id mapping cached for dmp patient id: %s", dmpPatientId));

        LOGGER.info(String.format("Converted dmp file entry: %s to dmp sample: %s", dmpFileEntry, dmpSample));

        return dmpSample;
    }

    public static class DMPSampleNotFoundException extends RuntimeException {
        public DMPSampleNotFoundException(String message) {
            super(message);
        }
    }

}
