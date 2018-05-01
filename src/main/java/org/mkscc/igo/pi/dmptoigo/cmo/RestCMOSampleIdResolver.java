package org.mkscc.igo.pi.dmptoigo.cmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mskcc.domain.external.ExternalSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestCMOSampleIdResolver implements CMOSampleIdResolver {
    private static final Logger LOGGER = LogManager.getLogger(RestCMOSampleIdResolver.class);

    @Autowired
    @Qualifier("limsRest")
    private RestTemplate restTemplate;

    @Value("${lims.rest.url}")
    private String limsRestUrl;

    @Value("${lims.rest.cmoid.endpoint}")
    private String getCmoIdEndpoint;

    @Override
    public String resolve(ExternalSample externalSample) {
        String url = String.format("%s/%s?sampleId=%s&patientId=%s&sampleClass=%s&sampleOrigin=%s&specimenType=%s" +
                        "&nucleidAcid=%s&counter=%d",
                limsRestUrl, getCmoIdEndpoint, externalSample.getExternalId(), externalSample.getPatientCmoId(),
                externalSample.getSampleClass(), externalSample.getSampleOrigin(), externalSample.getSpecimenType(),
                externalSample.getNucleidAcid(), externalSample.getCounter());

        ResponseEntity<String> cmoIdEntity = restTemplate.getForEntity(url, String.class);

        String cmoId = cmoIdEntity.getBody();

        LOGGER.info(String.format("Cmo sample id retrieved for sample %s: %s", externalSample.getExternalId(), cmoId));

        return cmoId;
    }
}
