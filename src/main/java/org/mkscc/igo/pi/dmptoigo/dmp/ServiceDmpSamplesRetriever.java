package org.mkscc.igo.pi.dmptoigo.dmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceDmpSamplesRetriever implements DmpSamplesRetriever {
    private static final Logger LOGGER = LogManager.getLogger(ServiceDmpSamplesRetriever.class);

    @Value("${dmp.service.url}")
    private String serviceUrl;

    @Value("${dmp.pcode.mrn.endpoint}")
    private String endpoint;

    @Autowired
    @Qualifier("basicRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public DmpPatient retrieve(String dmpPatientId) {
        LOGGER.info(String.format("Retrieving samples and information for DMP Patient ID: %s", dmpPatientId));

        String url = getUrl(dmpPatientId);

        LOGGER.info(String.format("Invoking DMP service method: %s", url));

        DmpPatient dmpPatient = restTemplate.getForObject(url, DmpPatient.class);

        return dmpPatient;
    }

    private String getUrl(String dmpPatientId) {
        String url = String.format("%s/%s/%s", serviceUrl, endpoint, dmpPatientId);

        return url;
    }
}
