package org.mkscc.igo.pi.dmptoigo.cmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mskcc.domain.patient.CRDBPatientInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestCMOPatientInfoRetriever implements CMOPatientInfoRetriever {
    private static final Logger LOGGER = LogManager.getLogger(RestCMOPatientInfoRetriever.class);

    @Value("${cmo.patient.service.url}")
    private String cmoPatientServiceUrl;

    @Value("${cmo.patient.id.endpoint}")
    private String patientIdEndpoint;

    @Autowired
    @Qualifier("cmoPatientRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public CRDBPatientInfo resolve(String mrn) {
        ResponseEntity<CRDBPatientInfo> crdbPatientInfoEntity = restTemplate.getForEntity(getUrl(mrn), CRDBPatientInfo
                .class);

        if (crdbPatientInfoEntity.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException(String.format("Unable to retrieve cmo patient id from service: %s. Cause: %s",
                    cmoPatientServiceUrl, crdbPatientInfoEntity.getBody()));
        return crdbPatientInfoEntity.getBody();
    }

    private String getUrl(String mrn) {
        String url = String.format("%s/%s/%s", cmoPatientServiceUrl, patientIdEndpoint, mrn);

        LOGGER.info(String.format("Retrieving CMO Patient id from service: %s for endpoint: %s", cmoPatientServiceUrl,
                patientIdEndpoint));

        return url;
    }
}
