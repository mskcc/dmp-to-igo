package org.mkscc.igo.pi.cmo;

import org.apache.log4j.Logger;
import org.mkscc.igo.pi.crdb.CRDBPatientInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CRDBMrnToCmoPatientIdResolver implements MrnToCmoPatientIdResolver {
    private static final Logger LOGGER = Logger.getLogger(CRDBMrnToCmoPatientIdResolver.class);

    @Value("${crdb.service.url}")
    private String crdbServiceUrl;

    @Value("${crdb.mrn.cmo.endpoint}")
    private String endpoint;

    @Value("${crdb.username}")
    private String username;

    @Value("${crdb.password}")
    private String password;

    @Value("${crdb.sid}")
    private String sid;

    @Override
    public String resolve(String mrn) {
        RestTemplate restTemplate = new RestTemplate();
        CRDBPatientInfo crdbPatientInfo = restTemplate.getForObject(getUrl(mrn), CRDBPatientInfo.class);

        String patientId = crdbPatientInfo.getPatientId();
        LOGGER.info(String.format("Retrieved CMO Patient id: %s", patientId));

        return patientId;
    }

    private String getUrl(String mrn) {
        String url = String.format("%s/%s?mrn=%s&un=%s&pw=%s&sid=%s", crdbServiceUrl, endpoint, mrn, username, password, sid);

        LOGGER.info(String.format("Retrieving CMO Patient id from service: %s for endpoint: %s", crdbServiceUrl, endpoint));

        return url;
    }
}
