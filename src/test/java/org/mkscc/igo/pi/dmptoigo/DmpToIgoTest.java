package org.mkscc.igo.pi.dmptoigo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalSampleRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPFileEntriesRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPFileEntryToSampleConverterFactory;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesGateway;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DmpFileEntryToSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mockito.runners.MockitoJUnitRunner;
import org.mskcc.domain.external.ExternalSample;
import org.mskcc.util.notificator.Notificator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DmpToIgoTest {
    private static ExternalSample externalSample0 = getExternalSample0();
    private static ExternalSample externalSample1 = getExternalSample1();
    private static ExternalSample externalSample2 = getExternalSample2();
    private final DMPFileEntryToSampleConverterFactory dmpFileEntryToSampleConverterFactory = mock
            (DMPFileEntryToSampleConverterFactory.class);
    private DMPSamplesGateway dmpSamplesGateway;
    private ExternalSampleRepositorySpy externalSampleRepositorySpy;
    private DMPFileEntriesRetriever dmpFileEntriesRetriever = mock(DMPFileEntriesRetriever.class);
    private Notificator notificator = mock(Notificator.class);

    private static ExternalSample getExternalSample0() {
        return new ExternalSample(0, "ext0", "extPat0", "file0.path", "run0", "sampClass0", "sampOrig0", "Tumor");
    }

    private static ExternalSample getExternalSample1() {
        return new ExternalSample(1, "ext1", "extPat1", "file1.path", "run1", "sampClass1", "sampOrig1", "Tumor");
    }

    private static ExternalSample getExternalSample2() {
        return new ExternalSample(2, "ext2", "extPat2", "file2.path", "run2", "sampClass2", "sampOrig2", "Tumor");
    }

    public DMPSampleToExternalSampleConverter getConverter() {
        return dmpSample -> {
            if (Objects.equals(dmpSample.getDmpId(), "P-1234567-T01-0"))
                return externalSample0;
            if (Objects.equals(dmpSample.getDmpId(), "P-1234567-T01-1"))
                return externalSample1;
            if (Objects.equals(dmpSample.getDmpId(), "P-1234567-T01-2"))
                return externalSample2;

            throw new RuntimeException(String.format("No DMP Sample %s found", dmpSample));
        };
    }

    @Before
    public void setUp() throws Exception {
        externalSampleRepositorySpy = new ExternalSampleRepositorySpy();
        when(dmpFileEntryToSampleConverterFactory.getConverter(any())).thenReturn(new
                DummyDMPSampleToSampleConverter());
        dmpSamplesGateway = new DMPSamplesGateway(dmpFileEntriesRetriever, getConverter(),
                dmpFileEntryToSampleConverterFactory, externalSampleRepositorySpy, notificator);
    }

    @Test
    public void whenThreeSamplesAreRetrieved_shouldSaveThreeSamples() throws Exception {
        //given
        int numberOfSamples = 3;
        when(dmpFileEntriesRetriever.retrieve()).thenReturn(getDmpFileEntries(numberOfSamples));

        //when
        dmpSamplesGateway.invoke();

        //then
        assertThat(externalSampleRepositorySpy.getAll().size()).isEqualTo(numberOfSamples);
    }

    private List<DmpFileEntry> getDmpFileEntries(int numberOfSamples) {
        List<DmpFileEntry> dmpFileEntries = new ArrayList<>();

        for (int i = 0; i < numberOfSamples; i++) {
            DmpFileEntry dmpFileEntry = new DmpFileEntry();
            dmpFileEntry.setDmpSampleId("P-1234567-T01-" + i);
            dmpFileEntries.add(dmpFileEntry);
        }

        return dmpFileEntries;
    }

    @Configuration
    class Config {
        @Bean
        public ExternalSampleRepositorySpy externalSampleSaver() {
            return new ExternalSampleRepositorySpy();
        }
    }

    private class ExternalSampleRepositorySpy implements ExternalSampleRepository {
        private List<ExternalSample> savedSamples = new ArrayList<>();

        @Override
        public boolean exists(String sampleId) {
            return savedSamples.stream()
                    .anyMatch(s -> Objects.equals(s.getExternalId(), sampleId));
        }

        @Override
        public void save(ExternalSample externalSample) {
            savedSamples.add(externalSample);
        }

        @Override
        public List<ExternalSample> getAll() {
            return savedSamples;
        }
    }

    private class DummyDMPSampleToSampleConverter implements DmpFileEntryToSampleConverter {
        @Override
        public DMPSample convert(DmpFileEntry dmpFileEntry) {
            DMPSample dmpSample = new DMPSample();
            dmpSample.setDmpId(dmpFileEntry.getDmpSampleId());
            return dmpSample;
        }
    }
}
