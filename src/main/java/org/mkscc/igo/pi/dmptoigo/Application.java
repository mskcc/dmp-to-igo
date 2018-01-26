package org.mkscc.igo.pi.dmptoigo;

import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesGateway;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesRetriever;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        DMPSamplesGateway dmpSamplesGateway = context.getBean(DMPSamplesGateway.class);
        return args -> dmpSamplesGateway.invoke();
    }
}
