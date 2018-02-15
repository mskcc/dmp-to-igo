package org.mkscc.igo.pi.dmp;

import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.config.AppConfig;
import org.mkscc.igo.pi.dmptoigo.dmp.AnnonymizedRunId2RunIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.BamPathRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.CachingDMPFileEntryToSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.sample.ExternalSample;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import(AppConfig.class)
@ComponentScan(basePackages = "org.mskcc.igo.pi")
public class TestAppConfiguration {
    public static final String externalId1 = "P-123456-FHDJDS";
    public static final String externalId2 = "P-789789-AAABBB";
    public static ExternalSample externalSample1 = getExternalSample1();

    public static ExternalSample externalSample2 = getExternalSample2();

    public static String cmoId1 = "C-123456-N001-dZ";
    public static String cmoId2 = "C-444666-X003-rZ";

    @MockBean
    private AnnonymizedRunId2RunIdRepository annonymizedRunId2RunIdRepository;

    @Bean
    public DMPSamplesRetriever<DMPSample> dmpSamplesRetriever() {
        return new DMPSamplesRetriever<DMPSample>() {
            @Override
            public List<DMPSample> retrieve() {
                return Arrays.asList(new DMPSample());
            }
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

    private static ExternalSample getExternalSample1() {
        return new ExternalSample("JAX_444", "/it/is/bam.bam", externalId1, "P-123456");
    }

    private static ExternalSample getExternalSample2() {
        return new ExternalSample("JAX_444", "/it/is/bam2.bam", externalId2, "P-789789");
    }

    public static DmpPatientId2CMOPatientIdRepository getPatientRepository() {
        DmpPatientId2CMOPatientIdRepository repository = new DmpPatientId2CMOPatientIdRepository();
        repository.put("P-123456", "C-987654");

        return repository;
    }

    public static CMOSampleIdResolver getCMOSampleIdResolver() {
        CMOSampleIdResolver cmoSampleIdResolver = mock(CMOSampleIdResolver.class);

        when(cmoSampleIdResolver.resolve(externalSample1)).thenReturn(cmoId1);
        when(cmoSampleIdResolver.resolve(externalSample2)).thenReturn(cmoId2);

        return cmoSampleIdResolver;
    }
}
