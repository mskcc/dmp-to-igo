package org.mkscc.igo.pi.dmptoigo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.mkscc.igo.pi")
public class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        try {
            DMPSamplesGateway dmpSamplesGateway = context.getBean(DMPSamplesGateway.class);
            return args -> dmpSamplesGateway.invoke();
        } catch (Exception e) {
            LOGGER.error("Error while retrieving dmp samples", e);
            return args -> {
            };
        }
    }
}
