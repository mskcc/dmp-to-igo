package org.mkscc.igo.pi.dmptoigo.dmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.DMPSamplesGateway;
import org.mskcc.util.notificator.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;

@Controller
public class DmpToIgoController {
    private static final Logger LOGGER = LogManager.getLogger(DmpToIgoController.class);

    private DMPSamplesGateway dmpSamplesGateway;
    private Notificator notificator;

    @Autowired
    public DmpToIgoController(DMPSamplesGateway dmpSamplesGateway, Notificator notificator) {
        this.dmpSamplesGateway = dmpSamplesGateway;
        this.notificator = notificator;
    }

    @RequestMapping("/dmp-to-igo")
    public String retrieve() {
        LOGGER.info("Running DMP to IGO samples");

        try {
            dmpSamplesGateway.invoke();
            return "DMP samples converted to IGO";
        } catch (Exception e) {
            LOGGER.error("Error while retrieving dmp samples", e);
            tryToNotifyOfErrors(e.getMessage());
            return String.format("Error converting DMP samples to IGO: %s", e);
        }
    }

    private void tryToNotifyOfErrors(String message) {
        try {
            notificator.notifyMessage("", message);
        } catch (Exception e) {
            LOGGER.warn("Unable to send notification about errors in converting dmp samples", e);
        }
    }
}
