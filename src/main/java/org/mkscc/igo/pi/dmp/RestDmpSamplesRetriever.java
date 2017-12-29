package org.mkscc.igo.pi.dmp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestDmpSamplesRetriever implements DmpSamplesRetriever {
    private static final Logger LOGGER = Logger.getLogger(RestDmpSamplesRetriever.class);

    @Value("${dmp.service.url}")
    private String serviceUrl;

    @Value("${dmp.pcode.mrn.endpoint}")
    private String endpoint;

    @Override
    public DmpPatientWithSamples retrieve(String dmpPatientId) {
        LOGGER.info(String.format("Resolving MRN for DMP Patient ID: %s", dmpPatientId));

        RestTemplate restTemplate = new RestTemplate();
        DmpPatientWithSamples dmpPatientWithSamples = restTemplate.getForObject(getUrl(dmpPatientId), DmpPatientWithSamples.class);

        LOGGER.info(String.format("Resolved MRN for DMP Patient ID: %s", dmpPatientId));

        return dmpPatientWithSamples;
    }

    private String getUrl(String dmpPatientId) {
        String url = String.format("%s/%s/%s", serviceUrl, endpoint, dmpPatientId);
        LOGGER.info(String.format("Invoking DMP service method: %s", url));

        return url;
    }
}
