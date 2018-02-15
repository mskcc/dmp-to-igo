package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.ExternalSampleSaver;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.sample.ExternalSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DMPSamplesGateway {
    private static final Logger LOGGER = LogManager.getLogger(DMPSamplesGateway.class);

    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;
    private DMPSamplesRetriever<DMPSample> dmpSamplesRetriever;
    private ExternalSampleSaver externalSampleSaver;

    @Autowired
    public DMPSamplesGateway(DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter,
                             DMPSamplesRetriever<DMPSample> dmpSamplesRetriever, ExternalSampleSaver
                                     externalSampleSaver) {
        this.dmpSampleToExternalSampleConverter = dmpSampleToExternalSampleConverter;
        this.dmpSamplesRetriever = dmpSamplesRetriever;
        this.externalSampleSaver = externalSampleSaver;
    }

    public void invoke() {
        List<DMPSample> dmpSamples = dmpSamplesRetriever.retrieve();

        LOGGER.info("Saving dmp samples to IGO external samples");
        for (DMPSample dmpSample : dmpSamples) {
            ExternalSample externalSample = convert(dmpSample);
            externalSampleSaver.save(externalSample);
        }
    }

    private ExternalSample convert(DMPSample sample) {
        return dmpSampleToExternalSampleConverter.convert(sample);
    }
}
