package org.mkscc.igo.pi.dmptoigo.dmp;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOPatientInfoRetriever;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPTumorNormal;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.SampleType;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.external.ExternalSample;
import org.mskcc.domain.patient.CRDBPatientInfo;
import org.mskcc.domain.sample.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleDMPSampleToExternalSampleConverter implements DMPSampleToExternalSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(SimpleDMPSampleToExternalSampleConverter.class);

    private final CMOSampleIdResolver cmoSampleIdResolver;
    private final DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;
    private DMPGenderToIgoSexConverter dmpGenderToIgoSexConverter;
    private CMOPatientInfoRetriever cmoPatientIdRetriever;

    @Autowired
    public SimpleDMPSampleToExternalSampleConverter(
            CMOSampleIdResolver cmoSampleIdResolver,
            DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository,
            DMPGenderToIgoSexConverter dmpGenderToIgoSexConverter,
            CMOPatientInfoRetriever cmoPatientIdRetriever) {
        this.cmoSampleIdResolver = cmoSampleIdResolver;
        this.dmpPatientId2CMOPatientIdRepository = dmpPatientId2CMOPatientIdRepository;
        this.dmpGenderToIgoSexConverter = dmpGenderToIgoSexConverter;
        this.cmoPatientIdRetriever = cmoPatientIdRetriever;
    }

    @Override
    public ExternalSample convert(DMPSample dmpSample) {
        LOGGER.info(String.format("Converting dmp sample: %s to External Sample", dmpSample));

        TumorNormalType tumorNormal = getTumorNormal(dmpSample.getTumorNormal());
        ExternalSample externalSample = new ExternalSample(
                dmpSample.getCounter(),
                dmpSample.getDmpId(),
                dmpSample.getPatientDmpId(),
                dmpSample.getBamPath(),
                dmpSample.getAnnonymizedRunID(),
                getSampleClass(dmpSample),
                getSampleOrigin(tumorNormal),
                tumorNormal.getValue()
        );

        externalSample.setNucleidAcid(DefaultValues.DEFAULT_NUCLEID_ACID.getValue());
        externalSample.setPatientCmoId(getCmoPatientId(dmpSample.getPatientDmpId()));
        externalSample.setSpecimenType(getSpecimenType(dmpSample));
        externalSample.setCmoId(cmoSampleIdResolver.resolve(externalSample));
        externalSample.setBaitVersion(DefaultValues.DEFAULT_BAIT_VERSION.getValue());
        externalSample.setSex(getSex(dmpSample));
        externalSample.setOncotreeCode(dmpSample.getOncotreeCode());
        externalSample.setTissueSite(getTissueSite(dmpSample));
        externalSample.setPreservationType(getPreservationType(dmpSample));

        LOGGER.info(String.format("Dmp Sample %s converted to External Sample: %s", dmpSample.getDmpId(),
                externalSample));

        return externalSample;
    }

    private String getTissueSite(DMPSample dmpSample) {
        try {
            if (SampleType.getByValue(dmpSample.getSampleType()) == SampleType.PRIMARY)
                return dmpSample.getPrimarySite();
            return dmpSample.getMetastatisSite();
        } catch (Exception e) {
            LOGGER.warn(String.format("Tissue site for dmp sample %s could not be retrieved this left empty",
                    dmpSample.getDmpId()));
            return "";
        }
    }

    private String getSex(DMPSample dmpSample) {
        try {
            return dmpGenderToIgoSexConverter.convert(dmpSample.getGender()).toString();
        } catch (Exception e) {
            LOGGER.warn(String.format("Sex for dmp sample %s could not be retrieved this left empty", dmpSample
                    .getDmpId()));
            return "";
        }
    }

    private String getPreservationType(DMPSample dmpSample) {
        return dmpSample.isTumor() ? DefaultValues.DEFAULT_TUMOR_PRESERVATION : DefaultValues
                .DEFAULT_NORMAL_PRESERVATION;
    }

    private String getSampleClass(DMPSample dmpSample) {
        if (!dmpSample.isTumor())
            return DefaultValues.DEFAULT_NORMAL_SAMPLE_CLASS.getValue();

        if (StringUtils.isEmpty(dmpSample.getSampleType()))
            return "";
        return SampleType.getByValue(dmpSample.getSampleType()).getSampleClass().getValue();
    }

    private String getSpecimenType(DMPSample dmpSample) {
        return dmpSample.isTumor() ? DefaultValues.DEFAULT_TUMOR_SPECIMEN_TYPE.getValue() :
                DefaultValues.DEFAULT_NORMAL_SPECIMEN_TYPE.getValue();
    }

    private String getSampleOrigin(TumorNormalType tumorNormal) {
        return tumorNormal == TumorNormalType.NORMAL ? DefaultValues.DEFAULT_NORMAL_SAMPLE_ORIGIN.getValue() :
                DefaultValues.DEFAULT_TUMOR_SAMPLE_ORIGIN.getValue();
    }

    private String getCmoPatientId(String dmpPatientId) {
        if(dmpPatientId2CMOPatientIdRepository.containsKey(dmpPatientId)) {
            String cmoPatientId = dmpPatientId2CMOPatientIdRepository.getCMOPatientIdByDMPPatientId
                    (dmpPatientId);
            LOGGER.info(String.format("CMO Patient id %s found in cache for DMP id: %s", cmoPatientId, dmpPatientId));
            return cmoPatientId;
        }

        CRDBPatientInfo crdbPatientInfo = cmoPatientIdRetriever.resolve(dmpPatientId);

        return crdbPatientInfo.getPatientId();
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
        public static final String DEFAULT_TUMOR_PRESERVATION = "FFPE";
        public static final String DEFAULT_NORMAL_PRESERVATION = "EDTA-Streck";
        public static final Recipe DEFAULT_BAIT_VERSION = Recipe.IMPACT_468;
    }
}
