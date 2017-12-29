package org.mkscc.igo.pi.dmp.converter;

import org.apache.log4j.Logger;
import org.mkscc.igo.pi.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.cmo.MrnToCmoPatientIdResolver;
import org.mkscc.igo.pi.dmp.DmpPatientWithSamples;
import org.mkscc.igo.pi.dmp.DmpSamplesRetriever;
import org.mkscc.igo.pi.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmp.domain.DmpFileEntry;
import org.mkscc.igo.pi.dmp.domain.SampleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CmoPatientIdAwareDmpFileEntryToSampleConverter implements DmpFileEntryToSampleConverter<DMPSample> {
    private static final Logger LOGGER = Logger.getLogger(CmoPatientIdAwareDmpFileEntryToSampleConverter.class);

    private final MrnToCmoPatientIdResolver mrnToCmoPatientIdResolver;
    private final DmpSamplesRetriever dmpSamplesRetriever;
    private final CMOSampleIdResolver cmoSampleIdResolver;

    @Autowired
    public CmoPatientIdAwareDmpFileEntryToSampleConverter(MrnToCmoPatientIdResolver mrnToCmoPatientIdResolver,
                                                          DmpSamplesRetriever dmpSamplesRetriever,
                                                          CMOSampleIdResolver cmoSampleIdResolver) {
        this.mrnToCmoPatientIdResolver = mrnToCmoPatientIdResolver;
        this.dmpSamplesRetriever = dmpSamplesRetriever;
        this.cmoSampleIdResolver = cmoSampleIdResolver;
    }

    @Override
    public DMPSample convert(DmpFileEntry dmpFileEntry) {
        LOGGER.info(String.format("Converting dmp sample: %s to IGO sample", dmpFileEntry.getDmpSampleId()));

        String dmpPatientId = getDMPPatientId(dmpFileEntry.getDmpSampleId());
        DmpPatientWithSamples dmpPatientWithSamples = dmpSamplesRetriever.retrieve(dmpPatientId);

        Optional<DMPSample> optionalDmpSample = dmpPatientWithSamples.getSamples().stream()
                .filter(s -> s.getIgoId().equals(dmpFileEntry.getDmpSampleId())).findFirst();

        if(!optionalDmpSample.isPresent())
            throw new DMPSampleNotFoundException(String.format("DMP Sample with id: %s was not found.", dmpFileEntry.getDmpSampleId()));

        DMPSample dmpSample = optionalDmpSample.get();

        dmpSample.setSampleClass(getSampleClass(dmpFileEntry));
        dmpSample.setSampleOrigin(DMPSample.DEFAULT_SAMPLE_ORIGIN.getValue());
        dmpSample.setNAtoExtract(DMPSample.DEFAULT_NUCLEID_ACID.getValue());
        dmpSample.setSpecimenType(DMPSample.DEFAULT_SPECIMEN_TYPE.getValue());
        dmpSample.setCmoPatientId(getCmoPatientId(dmpPatientWithSamples.getMrn()));
        dmpSample.setCmoSampleId(getCmoSampleId(dmpSample));
        dmpSample.setPatientId(dmpPatientId);

        LOGGER.info(String.format("Converted dmp sample: %s to IGO sample", dmpSample.getIgoId()));

        return dmpSample;
    }

    private String getCmoSampleId(DMPSample dmpSample) {
        return cmoSampleIdResolver.resolve(dmpSample);
    }

    private String getSampleClass(DmpFileEntry dmpFileEntry) {
        if (Objects.equals(dmpFileEntry.getSampleType(), "-"))
            return "";
        int sampleType = Integer.parseInt(dmpFileEntry.getSampleType());

        return SampleType.getByValue(sampleType).getSampleClass().getValue();
    }

    private String getDMPPatientId(String dmpId) {
        Pattern pattern = Pattern.compile("(P-[0-9]{7})-.*");
        Matcher matcher = pattern.matcher(dmpId);

        if (matcher.matches())
            return matcher.group(1);

        throw new IncorrectPatientIdFormatException(String.format("Incorrect format of dmp id: %s. Expected format is: %s",
                dmpId, pattern.pattern()));
    }

    private String getCmoPatientId(String mrn) {
        String cmoId = mrnToCmoPatientIdResolver.resolve(mrn);

        LOGGER.info(String.format("Retrieved CMO Patient id: %s", cmoId));

        return cmoId;
    }

    static class DMPSampleNotFoundException extends RuntimeException {
        public DMPSampleNotFoundException(String message) {
            super(message);
        }
    }

    static class IncorrectPatientIdFormatException extends RuntimeException {
        public IncorrectPatientIdFormatException(String message) {
            super(message);
        }
    }
}
