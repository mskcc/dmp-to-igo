package org.mkscc.igo.pi.external.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalSampleRepository;
import org.mskcc.domain.external.ExternalSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceExternalSampleRepository implements ExternalSampleRepository {
    private static final Logger LOGGER = LogManager.getLogger(ServiceExternalSampleRepository.class);

    @Value("${external.sample.rest.url}")
    private String externalSampleRestUrl;

    @Value("${external.sample.rest.samples.endpoint}")
    private String samplesEndpoint;

    @Autowired
    @Qualifier("externalSampleRest")
    private RestTemplate restTemplate;

    @Override
    public boolean exists(String sampleId) {
        String url = String.format("%s/%s/%s", externalSampleRestUrl, samplesEndpoint, sampleId);

        ResponseEntity<ExternalSample[]> externalSampleResponse = restTemplate.getForEntity(url,
                ExternalSample[].class);

        boolean sampleExists = externalSampleResponse.getStatusCode() != HttpStatus.NOT_FOUND &&
                externalSampleResponse.getBody().length > 0;

        return sampleExists;
    }

    @Override
    public void save(ExternalSample externalSample) {
        LOGGER.info(String.format("Saving sample: %s", externalSample));

        HttpEntity<ExternalSample> request = new HttpEntity<>(externalSample);

        String url = String.format("%s/%s", externalSampleRestUrl, samplesEndpoint);
        ResponseEntity<ExternalSample> responseEntity = restTemplate.postForEntity(url, request, ExternalSample.class);

        HttpStatus statusCode = responseEntity.getStatusCode();
        LOGGER.info(String.format("Sample %s saving status: %s", externalSample.getExternalId(), statusCode));
    }
}
