package org.mkscc.igo.pi.dmptoigo.config;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mskcc.util.notificator.Notificator;
import org.mskcc.util.notificator.SlackNotificator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "org.mskcc.igo.pi")
public class AppConfig {
    @Value("${external.sample.rest.username}")
    private String externalSampleRestUsername;

    @Value("${external.sample.rest.password}")
    private String externalSampleRestPassword;

    @Value("${lims.rest.username}")
    private String limsRestUsername;

    @Value("${lims.rest.password}")
    private String limsRestPassword;

    @Value("${cmo.patient.rest.username}")
    private String cmoPatientRestUsername;

    @Value("${cmo.patient.rest.password}")
    private String cmoPatientRestPassword;

    @Value("${slack.webhookUrl}")
    private String webhookUrl;

    @Value("${slack.channel}")
    private String channel;

    @Value("${slack.user}")
    private String user;

    @Value("${slack.icon}")
    private String icon;

    @Value("${dmp.bam.mapping.file.path}")
    private String dmpBamMappingFilePath;

    @Bean
    @Qualifier("limsRest")
    public RestTemplate limsRestTemplate() {
        RestTemplate restTemplate = getInsecureRestTemplate();
        addBasicAuth(restTemplate, limsRestUsername, limsRestPassword);

        return restTemplate;
    }

    /**
     * Using insecure Rest Template to connect to Lims Rest becasue of certificate issues.
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     */
    private RestTemplate getInsecureRestTemplate() {
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            return new RestTemplate(requestFactory);
        } catch (Exception e) {
            throw new RuntimeException("Error while initializing insecure rest template", e);
        }
    }

    @Bean
    @Qualifier("externalSampleRest")
    public RestTemplate externalRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        addBasicAuth(restTemplate, externalSampleRestUsername, externalSampleRestPassword);
        restTemplate.setErrorHandler(externalSampleRestErrorHandler());

        return restTemplate;
    }

    @Bean
    public ResponseErrorHandler externalSampleRestErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                response.getStatusText();
            }
        };
    }

    @Bean
    @Qualifier("basicRestTemplate")
    public RestTemplate basicRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate;
    }

    @Bean
    @Qualifier("cmoPatientRestTemplate")
    public RestTemplate cmoPatientRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        addBasicAuth(restTemplate, cmoPatientRestUsername, cmoPatientRestPassword);

        return restTemplate;
    }

    private void addBasicAuth(RestTemplate restTemplate, String username, String password) {
        List<ClientHttpRequestInterceptor> interceptors = Collections.singletonList(new BasicAuthorizationInterceptor
                (username, password));
        restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(),
                interceptors));
    }

    @Bean
    public Notificator notificator() {
        return new SlackNotificator(webhookUrl, channel, user, icon);
    }
}
