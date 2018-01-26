package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.sample.ExternalSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class DMPSamplesGateway {
    private static final Logger LOGGER = LogManager.getLogger(DMPSamplesGateway.class);

    @Value("${external.sample.rest.url}")
    private String externalSampleRestUrl;

    @Value("${external.sample.rest.samples.endpoint}")
    private String samplesEndpoint;

    @Autowired
    @Qualifier("externalSampleRest")
    private RestTemplate restTemplate;

    @Autowired
    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;

    @Autowired
    private DMPSamplesRetriever<DMPSample> dmpSamplesRetriever;

    public void invoke() {
        List<DMPSample> dmpSamples = dmpSamplesRetriever.retrieve();

        LOGGER.info("Saving dmp samples to IGO external samples");
        for (DMPSample dmpSample : dmpSamples) {
            ExternalSample externalSample = convert(dmpSample);
            save(externalSample);
        }
    }

    private void save(ExternalSample externalSample) {
        LOGGER.info(String.format("Saving sample: %s", externalSample.getExternalId()));

        HttpEntity<ExternalSample> request = new HttpEntity<>(externalSample);

        String url = String.format("%s/%s", externalSampleRestUrl, samplesEndpoint);
        ResponseEntity<ExternalSample> responseEntity = restTemplate.postForEntity(url, request, ExternalSample.class);

        HttpStatus statusCode = responseEntity.getStatusCode();
        LOGGER.info(String.format("Sample %s saving status: %s", externalSample.getExternalId(), statusCode));
    }

    private ExternalSample convert(DMPSample sample) {
        return dmpSampleToExternalSampleConverter.convert(sample);
    }
}
