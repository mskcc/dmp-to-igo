package org.mkscc.igo.pi.dmptoigo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Collections;
import java.util.List;

@Configuration
public class AppConfig {
    @Value("${external.sample.rest.username}")
    private String externalSampleRestUsername;

    @Value("${external.sample.rest.password}")
    private String externalSampleRestPassword;

    @Value("${lims.rest.username}")
    private String limsRestUsername;

    @Value("${lims.rest.password}")
    private String limsRestPassword;

    @Bean
    @Qualifier("limsRest")
    public RestTemplate limsRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        addBasicAuth(restTemplate, limsRestUsername, limsRestPassword);

        return restTemplate;
    }

    @Bean
    @Qualifier("externalSampleRest")
    public RestTemplate externalRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        addBasicAuth(restTemplate, externalSampleRestUsername, externalSampleRestPassword);

        return restTemplate;
    }

    @Bean
    @Qualifier("basicRestTemplate")
    public RestTemplate basicRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate;
    }

    private void addBasicAuth(RestTemplate restTemplate, String username, String password) {
        List<ClientHttpRequestInterceptor> interceptors = Collections.singletonList(new BasicAuthorizationInterceptor
                (username, password));
        restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(),
                interceptors));
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setAfterMessagePrefix("Request data : ");

        return filter;
    }
}
