package org.mkscc.igo.pi;

import org.apache.log4j.Logger;
import org.mkscc.igo.pi.dmp.converter.IgoSamplesRetriever;
import org.mkscc.igo.pi.dmp.domain.DMPSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class);

    @Autowired
    private IgoSamplesRetriever igoSamplesRetriever;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            List<DMPSample> dmpSamples = igoSamplesRetriever.retrieve();

            for (DMPSample dmpSample : dmpSamples) {
                if (!exists(dmpSample)) {
                    LOGGER.info(String.format("Saving DMP sample: %s", dmpSample.getIgoId()));
                    save(dmpSample);
                } else {
                    LOGGER.info(String.format("Sample: %s already exists. It won't be saved.", dmpSample.getIgoId()));
                }
            }
        };
    }

    private boolean exists(DMPSample sample) {
        return false;
    }

    private void save(DMPSample sample) {
        // save to DB
    }
}
