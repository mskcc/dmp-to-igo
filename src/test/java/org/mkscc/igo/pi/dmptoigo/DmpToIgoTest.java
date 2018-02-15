package org.mkscc.igo.pi.dmptoigo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mkscc.igo.pi.dmptoigo.cmo.ExternalSampleSaver;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesGateway;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mskcc.domain.sample.ExternalSample;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DmpToIgoTest {
    @InjectMocks
    private DMPSamplesGateway dmpSamplesGateway;

    @Mock
    private DMPSamplesRetriever<DMPSample> dmpSampleDMPSamplesRetriever;

    private ExternalSampleSaverSpy externalSampleSaverSpy;

    @Mock
    private DMPSampleToExternalSampleConverter converterMock;

    @Before
    public void setUp() throws Exception {
        externalSampleSaverSpy = new ExternalSampleSaverSpy();
        dmpSamplesGateway = new DMPSamplesGateway(converterMock, dmpSampleDMPSamplesRetriever, externalSampleSaverSpy);
    }

    @Test
    public void whenThreeSamplesAreRetrieved_shouldSaveThreeSamples() throws Exception {
        //given
        int numberOfSamples = 3;
        when(dmpSampleDMPSamplesRetriever.retrieve()).thenReturn(getDmpSamples(numberOfSamples));

        //when
        dmpSamplesGateway.invoke();

        //then
        assertThat(externalSampleSaverSpy.getSavedSamples().size()).isEqualTo(numberOfSamples);
    }

    private List<DMPSample> getDmpSamples(int numberOfSamples) {
        List<DMPSample> dmpSamples = new ArrayList<>();

        for (int i = 0; i < numberOfSamples; i++) {
            dmpSamples.add(new DMPSample());
        }

        return dmpSamples;
    }

    @Configuration
    class Config {
        @Bean
        public ExternalSampleSaverSpy externalSampleSaver() {
            return new ExternalSampleSaverSpy();
        }
    }

    private class ExternalSampleSaverSpy implements ExternalSampleSaver {
        private List<ExternalSample> savedSamples = new ArrayList<>();

        @Override
        public void save(ExternalSample externalSample) {
            savedSamples.add(externalSample);
        }

        public List<ExternalSample> getSavedSamples() {
            return savedSamples;
        }
    }


}
