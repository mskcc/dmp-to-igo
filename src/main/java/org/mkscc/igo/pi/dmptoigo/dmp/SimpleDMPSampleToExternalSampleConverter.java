package org.mkscc.igo.pi.dmptoigo.dmp;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalRunIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPTumorNormal;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.SampleType;
import org.mskcc.domain.external.ExternalSample;
import org.mskcc.domain.sample.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleDMPSampleToExternalSampleConverter implements DMPSampleToExternalSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(SimpleDMPSampleToExternalSampleConverter.class);

    private final CMOSampleIdResolver cmoSampleIdResolver;
    private final DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;
    private ExternalRunIdRepository externalRunIdRepository;

    @Autowired
    public SimpleDMPSampleToExternalSampleConverter(CMOSampleIdResolver cmoSampleIdResolver,
                                                    DmpPatientId2CMOPatientIdRepository
                                                            dmpPatientId2CMOPatientIdRepository,
                                                    ExternalRunIdRepository externalRunIdRepository) {
        this.cmoSampleIdResolver = cmoSampleIdResolver;
        this.dmpPatientId2CMOPatientIdRepository = dmpPatientId2CMOPatientIdRepository;
        this.externalRunIdRepository = externalRunIdRepository;
    }

    @Override
    public ExternalSample convert(DMPSample dmpSample) {
        LOGGER.info(String.format("Converting dmp sample: %s to External Sample", dmpSample));

        fillInRunIdIfEmpty(dmpSample);

        TumorNormalType tumorNormal = getTumorNormal(dmpSample.getTumorNormal());
        ExternalSample externalSample = new ExternalSample(
                dmpSample.getCounter(),
                dmpSample.getDmpId(),
                dmpSample.getPatientDmpId(),
                dmpSample.getBamPath(),
                dmpSample.getRunID(),
                getSampleClass(tumorNormal, dmpSample.getSampleType()),
                getSampleOrigin(tumorNormal),
                tumorNormal.getValue()
        );

        externalSample.setNucleidAcid(DefaultValues.DEFAULT_NUCLEID_ACID.getValue());
        externalSample.setPatientCmoId(getCmoPatientId(dmpSample.getPatientDmpId()));
        externalSample.setSpecimenType(getSpecimenType(tumorNormal));
        externalSample.setCmoId(cmoSampleIdResolver.resolve(externalSample));

        LOGGER.info(String.format("Dmp Sample %s converted to External Sample: %s", dmpSample.getDmpId(),
                externalSample));

        return externalSample;
    }

    private void fillInRunIdIfEmpty(DMPSample dmpSample) {
        if (StringUtils.isEmpty(dmpSample.getRunID())) {
            LOGGER.debug(String.format("Filling in run id for dmp sample: %s", dmpSample.getDmpId()));

            String runId = externalRunIdRepository.getRunIdByAnonymizedRunId(dmpSample
                    .getAnnonymizedRunID());

            LOGGER.debug(String.format("Run id %s found for dmp sample: %s ", runId, dmpSample.getDmpId()));

            dmpSample.setRunID(runId);
        }
    }

    private String getSampleClass(TumorNormalType tumorNormalType, String sampleType) {
        if (tumorNormalType == TumorNormalType.NORMAL)
            return DefaultValues.DEFAULT_NORMAL_SAMPLE_CLASS.getValue();

        if (StringUtils.isEmpty(sampleType))
            return "";
        return SampleType.getByValue(sampleType).getSampleClass().getValue();
    }

    private String getSpecimenType(TumorNormalType tumorNormal) {
        return tumorNormal == TumorNormalType.NORMAL ? DefaultValues.DEFAULT_NORMAL_SPECIMEN_TYPE.getValue() :
                DefaultValues.DEFAULT_TUMOR_SPECIMEN_TYPE.getValue();
    }

    private String getSampleOrigin(TumorNormalType tumorNormal) {
        return tumorNormal == TumorNormalType.NORMAL ? DefaultValues.DEFAULT_NORMAL_SAMPLE_ORIGIN.getValue() :
                DefaultValues.DEFAULT_TUMOR_SAMPLE_ORIGIN.getValue();
    }

    private String getCmoPatientId(String dmpPatientId) {
        return dmpPatientId2CMOPatientIdRepository.getCMOPatientIdByDMPPatientId(dmpPatientId);
    }

    private TumorNormalType getTumorNormal(String tumorNormal) {
        return DMPTumorNormal.getByValue(tumorNormal).getIgoValue();
    }

    static class DefaultValues {
        public static final SampleOrigin DEFAULT_NORMAL_SAMPLE_ORIGIN = SampleOrigin.WHOLE_BLOOD;
        public static final SampleOrigin DEFAULT_TUMOR_SAMPLE_ORIGIN = SampleOrigin.TISSUE;
        public static final NucleicAcid DEFAULT_NUCLEID_ACID = NucleicAcid.DNA;
        public static final SpecimenType DEFAULT_NORMAL_SPECIMEN_TYPE = SpecimenType.BLOOD;
        public static final SpecimenType DEFAULT_TUMOR_SPECIMEN_TYPE = SpecimenType.BIOPSY;
        public static final SampleClass DEFAULT_NORMAL_SAMPLE_CLASS = SampleClass.NORMAL;
    }
}
