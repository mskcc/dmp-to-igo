package org.mkscc.igo.pi.external.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalRunIdRepository;
import org.mskcc.domain.external.ExternalRun;
import org.mskcc.util.notificator.Notificator;
import org.mskcc.util.rest.Header;
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
public class ServiceExternalRunIdRepository implements ExternalRunIdRepository {
    private static final Logger LOGGER = LogManager.getLogger(ServiceExternalRunIdRepository.class);

    @Value("${external.sample.rest.url}")
    private String externalRestUrl;

    @Value("${external.sample.rest.runs.endpoint}")
    private String runsEndpoint;

    @Autowired
    @Qualifier("externalSampleRest")
    private RestTemplate restTemplate;

    @Autowired
    private Notificator notificator;

    @Override
    public String getRunIdByAnonymizedRunId(String anonymizedRunId) {
        String url = String.format("%s/%s/%s", externalRestUrl, runsEndpoint, anonymizedRunId);

        ResponseEntity<ExternalRun> externalRunResponse = restTemplate.getForEntity(url, ExternalRun.class);

        if (externalRunResponse.getStatusCode() == HttpStatus.OK)
            return externalRunResponse.getBody().getRealRunId();

        return processError(anonymizedRunId, externalRunResponse);
    }

    private String processError(String anonymizedRunId, ResponseEntity<ExternalRun> externalRunResponse) {
        String errors = getErrors(externalRunResponse);

        if (externalRunResponse.getStatusCode() == HttpStatus.NOT_FOUND)
            throw new ExternalRunNotFoundException(String.format("External run with anonymized id: %s wasn't found. " +
                    "Cause: %s ", anonymizedRunId, errors));

        throw new ExternalRunRetrievalException(String.format("External run with anonymized id: %s couldn't have been" +
                " retrieved. Cause: %s", anonymizedRunId, errors));
    }

    @Override
    public void store(ExternalRun externalRun) {
        LOGGER.info(String.format("Saving run: %s", externalRun));

        HttpEntity<ExternalRun> request = new HttpEntity<>(externalRun);

        String url = String.format("%s/%s", externalRestUrl, runsEndpoint);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        HttpStatus statusCode = responseEntity.getStatusCode();

        if (statusCode != HttpStatus.OK) {
            String errors = getErrors(responseEntity);
            throw new ExternalRunStoringException(errors);
        } else {
            LOGGER.info(String.format("External Run %s saved. Status: %s", externalRun, statusCode));
        }
    }

    private <T> String getErrors(ResponseEntity<T> responseEntity) {
        List<String> errors = responseEntity.getHeaders().get(Header.ERRORS.name());
        return StringUtils.join(errors, System.lineSeparator());
    }

    public static class ExternalRunNotFoundException extends RuntimeException {
        public ExternalRunNotFoundException(String message) {
            super(message);
        }
    }

    private class ExternalRunStoringException extends RuntimeException {
        public ExternalRunStoringException(String message) {
            super(message);
        }
    }

    private class ExternalRunRetrievalException extends RuntimeException {
        public ExternalRunRetrievalException(String message) {
            super(message);
        }
    }
}
