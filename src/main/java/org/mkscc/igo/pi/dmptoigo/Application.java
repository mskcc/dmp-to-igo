package org.mkscc.igo.pi.dmptoigo;

import org.mkscc.igo.pi.dmptoigo.dmp.DmpToIgoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
//@ComponentScan(basePackages = {"org.mskcc.igo.pi", "org.mskcc.igo.pi.dmptoigo.dmp"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
