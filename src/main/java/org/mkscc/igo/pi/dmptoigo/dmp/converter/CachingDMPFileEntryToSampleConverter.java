package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.MrnToCmoPatientIdResolver;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientWithSamples;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpSamplesRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.AnnonymizedRunId2RunIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CachingDMPFileEntryToSampleConverter extends DmpFileEntryToSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(CachingDMPFileEntryToSampleConverter.class);

    @Autowired
    private DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;

    @Autowired
    private AnnonymizedRunId2RunIdRepository annonymizedRunId2RunIdRepository;

    @Autowired
    private DmpSamplesRetriever dmpSamplesRetriever;

    @Autowired
    private MrnToCmoPatientIdResolver mrnToCmoPatientIdResolver;

    @Override
    public DMPSample convertPart(DmpFileEntry dmpFileEntry, String dmpPatientId) {
        DmpPatientWithSamples dmpPatientWithSamples = dmpSamplesRetriever.retrieve(dmpPatientId);

        Optional<DMPSample> optionalDmpSample = dmpPatientWithSamples.getSamples().values().stream()
                .filter(s -> s.getDmpId().equals(dmpFileEntry.getDmpSampleId())).findFirst();

        if (!optionalDmpSample.isPresent()) {
            throw new DMPSampleNotFoundException(String.format("DMP " +
                    "Sample with id: %s was not found.", dmpFileEntry
                    .getDmpSampleId()));
        }

        DMPSample dmpSample = optionalDmpSample.get();
        String mrn = dmpPatientWithSamples.getMrn();
        String cmoPatientId = mrnToCmoPatientIdResolver.resolve(mrn);

        dmpPatientId2CMOPatientIdRepository.put(dmpPatientId, cmoPatientId);
        annonymizedRunId2RunIdRepository.put(dmpFileEntry.getAnnonymizedProjectName(), dmpSample.getRunID());

        LOGGER.info(String.format("Converted dmp file entry: %s to dmp sample", dmpSample));

        return dmpSample;
    }

    public static class DMPSampleNotFoundException extends RuntimeException {
        public DMPSampleNotFoundException(String message) {
            super(message);
        }
    }

}
