package org.mkscc.igo.pi.dmp;

import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.config.AppConfig;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.BamPathRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Objects;

import static org.mockito.Mockito.mock;

@Configuration
@Import(AppConfig.class)
@ComponentScan(basePackages = "org.mskcc.igo.pi")
public class TestAppConfiguration {
    public static final String externalId1 = "P-123456-FHDJDS";
    public static final String externalId2 = "P-789789-AAABBB";
    public static final String CACHED_DMP_PATIENT_ID = "P-123456";
    public static final String CACHED_CMO_PATIENT_ID = "C-987654";
    public static String cmoId1 = "C-123456-N001-dZ";
    public static String cmoId2 = "C-444666-X003-rZ";

    public static DmpPatientId2CMOPatientIdRepository getPatientRepository() {
        DmpPatientId2CMOPatientIdRepository repository = new DmpPatientId2CMOPatientIdRepository();
        repository.put(CACHED_DMP_PATIENT_ID, CACHED_CMO_PATIENT_ID);

        return repository;
    }

    public static CMOSampleIdResolver getCMOSampleIdResolver() {
        return externalSample -> {
            if (Objects.equals(externalSample.getExternalId(), externalId1))
                return cmoId1;
            if (Objects.equals(externalSample.getExternalId(), externalId2))
                return cmoId2;
            return "";
        };
    }

    @Bean
    public BamPathRetriever bamPathRetriever() {
        return annonymizedBamId -> "/some/path/to.bam";
    }

    @Bean
    public CMOSampleIdResolver cmoSampleIdResolver() {
        return mock(CMOSampleIdResolver.class);
    }

    @Bean
    public DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository() {
        return getPatientRepository();
    }
}
