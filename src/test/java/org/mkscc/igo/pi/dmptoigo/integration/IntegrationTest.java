package org.mkscc.igo.pi.dmptoigo.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mkscc.igo.pi.dmptoigo.Application;
import org.mkscc.igo.pi.dmptoigo.config.AppConfig;
import org.mkscc.igo.pi.external.rest.ServiceExternalRunIdRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        Application.class,
        AppConfig.class,
        ServiceExternalRunIdRepository.class
})
@ActiveProfiles("dev")
@EnableConfigurationProperties
@Profile("integrationTest")
public class IntegrationTest {
    @Resource
    private ServiceExternalRunIdRepository serviceExternalRunIdRepository;

    @Test
    public void when_should() throws Exception {
        //given
        //when
        //then
        assertThatExceptionOfType(ServiceExternalRunIdRepository.ExternalRunNotFoundException.class).isThrownBy(() ->
                serviceExternalRunIdRepository.getRunIdByAnonymizedRunId("notExistingOne"));
    }
}
