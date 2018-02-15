package org.mkscc.igo.pi.dmptoigo.cmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DelegatingCmoPatientIdRetriever implements CmoPatientIdRetriever {
    private static final Logger LOGGER = LogManager.getLogger(DelegatingCmoPatientIdRetriever.class);

    @Value("${cmo.patient.service.url}")
    private String cmoPatientServiceUrl;

    @Value("${cmo.patient.id.endpoint}")
    private String patientIdEndpoint;

    @Autowired
    @Qualifier("cmoPatientRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public String resolve(String mrn) {
        String cmoPatientId = restTemplate.getForObject(getUrl(mrn), String.class);
        return cmoPatientId;
    }

    private String getUrl(String mrn) {
        String url = String.format("%s/%s/%s", cmoPatientServiceUrl, patientIdEndpoint, mrn);

        LOGGER.info(String.format("Retrieving CMO Patient id from service: %s for endpoint: %s", cmoPatientServiceUrl,
                patientIdEndpoint));

        return url;
    }
}
