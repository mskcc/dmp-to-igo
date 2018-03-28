package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalSampleRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.external.ExternalSample;
import org.mskcc.util.notificator.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DMPSamplesGateway {
    private static final Logger LOGGER = LogManager.getLogger(DMPSamplesGateway.class);

    private ExternalSampleRepository externalSampleRepository;
    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;
    private DMPSamplesRetriever<DMPSample> dmpSamplesRetriever;
    private Notificator notificator;

    @Autowired
    public DMPSamplesGateway(DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter,
                             DMPSamplesRetriever<DMPSample> dmpSamplesRetriever,
                             ExternalSampleRepository externalSampleRepository,
                             Notificator notificator) {
        this.dmpSampleToExternalSampleConverter = dmpSampleToExternalSampleConverter;
        this.dmpSamplesRetriever = dmpSamplesRetriever;
        this.externalSampleRepository = externalSampleRepository;
        this.notificator = notificator;
    }

    public void invoke() {
        List<DMPSample> dmpSamples = dmpSamplesRetriever.retrieve();

        LOGGER.info("Saving dmp samples to IGO external samples");
        for (DMPSample dmpSample : dmpSamples) {
            try {
                tryToSave(dmpSample);
            } catch (Exception e) {
                logAndNotifyOfErrors(dmpSample, e);
            }
        }
    }

    private void tryToSave(DMPSample dmpSample) {
        LOGGER.info(String.format("Saving dmp sample: %s", dmpSample));

        ExternalSample externalSample = convert(dmpSample);
        externalSampleRepository.save(externalSample);
    }

    private void logAndNotifyOfErrors(DMPSample dmpSample, Exception e) {
        String message = String.format("DMP Sample %s couldn't be converted to External Sample and saved", dmpSample);
        LOGGER.warn(message, e);

        String messageWithCause = String.format("%s. Cause: %s", message, e.getMessage());
        tryToNotifyOrErrors(messageWithCause, dmpSample);
    }

    private void tryToNotifyOrErrors(String message, DMPSample dmpSample) {
        try {
            notificator.notifyMessage("", message);
        } catch (Exception e) {
            LOGGER.warn(String.format("Unable to send notification about errors in converting dmp sample: %s",
                    dmpSample), e);
        }
    }


    private ExternalSample convert(DMPSample sample) {
        return dmpSampleToExternalSampleConverter.convert(sample);
    }
}
