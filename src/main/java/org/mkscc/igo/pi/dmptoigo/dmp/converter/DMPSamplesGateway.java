package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalSampleRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.NoDMPToCMOPatientIdMapping;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mskcc.domain.external.ExternalSample;
import org.mskcc.util.notificator.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DMPSamplesGateway {
    private static final Logger LOGGER = LogManager.getLogger(DMPSamplesGateway.class);
    private static final String DMP_ID_PATTERN = "(P-[0-9]{7})-([TN])([0-9])+-([A-Za-z0-9]+)";

    private DMPFileEntriesRetriever dmpFileEntriesRetriever;
    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;
    private DMPFileEntryToSampleConverterFactory dmpFileEntryToSampleConverterFactory;
    private ExternalSampleRepository externalSampleRepository;
    private Notificator notificator;
    private Queue<DMPSample> samplesToProcess = new LinkedList<>();

    @Autowired
    public DMPSamplesGateway(DMPFileEntriesRetriever dmpFileEntriesRetriever,
                             DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter,
                             DMPFileEntryToSampleConverterFactory dmpFileEntryToSampleConverterFactory,
                             ExternalSampleRepository externalSampleRepository,
                             Notificator notificator) {
        this.dmpFileEntriesRetriever = dmpFileEntriesRetriever;
        this.dmpSampleToExternalSampleConverter = dmpSampleToExternalSampleConverter;
        this.dmpFileEntryToSampleConverterFactory = dmpFileEntryToSampleConverterFactory;
        this.externalSampleRepository = externalSampleRepository;
        this.notificator = notificator;
    }

    public void invoke() {
        try {
            saveDMPSamples();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving and saving DMP Samples. No samples will be saved", e);
        }
    }

    private void saveDMPSamples() throws FileNotFoundException {
        List<DmpFileEntry> dmpFileEntries = getDmpFileEntries();
        for (DmpFileEntry dmpFileEntry : dmpFileEntries) {
            String dmpId = dmpFileEntry.getDmpSampleId();

            if (!exists(dmpId)) {
                LOGGER.info(String.format("Converting dmp file entry to DMP Sample: %s", dmpId));
                saveDMPSample(dmpFileEntry, dmpId);
            } else {
                LOGGER.info(String.format("Sample with id %s already exists. It will be skipped.", dmpId));
            }
        }

        if (samplesToProcess.size() > 0) {
            LOGGER.info(String.format("Saving samples with previously missing CMO Patient ids: %s", samplesToProcess
                    .stream()
                    .map(DMPSample::getDmpId).collect(Collectors.joining(","))));

            DMPSample toProcess;
            while ((toProcess = samplesToProcess.poll()) != null) {
                tryToSave(toProcess);
            }
        }
    }

    private void saveDMPSample(DmpFileEntry dmpFileEntry, String dmpId) {
        try {
            dmpFileEntry.setDmpSampleIdView(retrieveDmpSampleIdView(dmpId));
            DMPSample dmpSample = convert(dmpFileEntry);
            tryToSave(dmpSample);
        } catch (Exception e) {
            logAndNofifyError(dmpId, e);
        }
    }

    private void tryToSave(DMPSample dmpSample) {
        try {
            LOGGER.info(String.format("Saving dmp sample: %s", dmpSample));

            ExternalSample externalSample = convert(dmpSample);
            LOGGER.info(String.format("Converted external sample: %s", externalSample));
            externalSampleRepository.save(externalSample);
        } catch (NoDMPToCMOPatientIdMapping e) {
            samplesToProcess.add(dmpSample);

            LOGGER.warn(String.format("DMP Sample %s couldn't be converted because of missing CMO patient id mapping." +
                    " It will be rerun after all samples are processed.", dmpSample.getDmpId()));
        } catch (Exception e) {
            logAndNotifyOfErrors(dmpSample, e);
        }
    }

    private void logAndNotifyOfErrors(DMPSample dmpSample, Exception e) {
        String message = String.format("DMP Sample %s couldn't be converted to External Sample and saved", dmpSample);
        LOGGER.error(message, e);

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

    private boolean exists(String sampleId) {
        return externalSampleRepository.exists(sampleId);
    }

    private void logAndNofifyError(String dmpSampleId, Exception e) {
        String message = String.format("File Entry for sample: %s couldn't be converted to DMP Sample. It " +
                "won't be saved", dmpSampleId);
        LOGGER.warn(message, e);

        String messageWithCause = String.format("%s. Cause: %s", message, e.getMessage());
        tryToNotifyOfErrors(messageWithCause, dmpSampleId);
    }

    private void tryToNotifyOfErrors(String message, String dmpSampleId) {
        try {
            notificator.notifyMessage("", message);
        } catch (Exception e) {
            LOGGER.error(String.format("Unable to send notification about errors in converting dmp sample: %s",
                    dmpSampleId), e);
        }
    }

    private List<DmpFileEntry> getDmpFileEntries() {
        return dmpFileEntriesRetriever.retrieve();
    }

    private DMPSample convert(DmpFileEntry dmpFileEntry) {
        DmpFileEntryToSampleConverter converter = dmpFileEntryToSampleConverterFactory.getConverter(dmpFileEntry);
        DMPSample dmpSample = converter.convert(dmpFileEntry);

        return dmpSample;
    }

    private DMPSampleIdView retrieveDmpSampleIdView(String dmpSampleId) {
        LOGGER.info(String.format("Parsing dmp sample id: %s", dmpSampleId));

        Pattern pattern = Pattern.compile(DMP_ID_PATTERN);
        Matcher matcher = pattern.matcher(dmpSampleId);

        validateDmpSampleId(dmpSampleId, matcher);

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId(matcher.group(1));
        dmpSampleIdView.setTumorNormal(matcher.group(2));
        dmpSampleIdView.setCounter(getCounter(dmpSampleId, matcher.group(3)));
        dmpSampleIdView.setAssay(matcher.group(4));

        LOGGER.info(String.format("Parsed dmp sample id: %s", dmpSampleIdView));

        return dmpSampleIdView;
    }

    private void validateDmpSampleId(String dmpSampleId, Matcher matcher) {
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Dmp Sample id: %s is in incorrect format. Expected " +
                    "format is: %s", dmpSampleId, DMP_ID_PATTERN));
    }

    private int getCounter(String dmpSampleId, String counter) {
        try {
            return Integer.parseInt(counter);
        } catch (Exception e) {
            throw new IncorrectDmpSampleCounterException(String.format("Unable to retrieve counter from dmp sample " +
                    "id: %s", dmpSampleId), e);
        }
    }

    static class IncorrectDmpSampleCounterException extends RuntimeException {
        public IncorrectDmpSampleCounterException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
