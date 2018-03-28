package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalRunIdRepository;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalSampleRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mskcc.util.notificator.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FromFileDMPSamplesRetriever implements DMPSamplesRetriever<DMPSample> {
    private static final Logger LOGGER = LogManager.getLogger(FromFileDMPSamplesRetriever.class);
    private static final String DMP_ID_PATTERN = "(P-[0-9]{7})-([TN])([0-9])+-([A-Za-z0-9]+)";

    @Value("${dmp.samples.file.path}")
    private String dmpSampleFilePath;

    @Autowired
    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;

    @Autowired
    private DMPFileEntryToSampleConverterFactory dmpFileEntryToSampleConverterFactory;

    @Autowired
    private ExternalRunIdRepository externalRunIdRepository;

    @Autowired
    private ExternalSampleRepository externalSampleRepository;

    @Autowired
    private Notificator notificator;

    private List<String> dmpIdsToProcess = new ArrayList<>();

    @Override
    public List<DMPSample> retrieve() {
        LOGGER.info(String.format("Retrieving samples from file: %s", dmpSampleFilePath));

        try {
            List<DmpFileEntry> dmpFileEntries = getDmpFileEntries();
            List<DMPSample> dmpSamples = new ArrayList<>();

            for (DmpFileEntry dmpFileEntry : dmpFileEntries) {
                String dmpId = dmpFileEntry.getDmpSampleId();

                if (!exists(dmpId)) {
                    LOGGER.info(String.format("Converting dmp file entry to DMP Sample: %s", dmpId));
                    try {
                        dmpFileEntry.setDmpSampleIdView(retrieveDmpSampleIdView(dmpId));

                        DMPSample dmpSample = convert(dmpFileEntry);
                        dmpSamples.add(dmpSample);
                    } catch (Exception e) {
                        logAndNofifyError(dmpId, e);
                    }
                } else {
                    LOGGER.info(String.format("Sample with id %s already exists. It will be skipped.", dmpId));
                }
            }

            fillRunIds(dmpSamples);

            return dmpSamples;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File with DMP samples not found: %s. No samples will be saved.",
                    dmpSampleFilePath), e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error while retrieving DMP Samples from file: %s. No samples " +
                    "will be saved", dmpSampleFilePath));
        }
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
            LOGGER.warn(String.format("Unable to send notification about errors in converting dmp sample: %s",
                    dmpSampleId), e);
        }
    }

    private void fillRunIds(List<DMPSample> dmpSamples) {
        LOGGER.info(String.format("Filling in mapped run ids"));
        for (DMPSample dmpSample : dmpSamples) {
            fillRunIdIfEmpty(dmpSample);
        }
    }

    private void fillRunIdIfEmpty(DMPSample dmpSample) {
        if (StringUtils.isEmpty(dmpSample.getRunID())) {
            try {
                String runId = externalRunIdRepository.getRunIdByAnonymizedRunId(dmpSample
                        .getAnnonymizedRunID());

                LOGGER.debug(String.format("Filling in run id for dmp sample: %s with value: %s", dmpSample.getDmpId
                        (), runId));

                dmpSample.setRunID(runId);
            } catch (Exception e) {
                LOGGER.warn(String.format("Unable to fill in run id for dmp sample: %s", dmpSample), e);
            }
        }
    }

    private List<DmpFileEntry> getDmpFileEntries() throws FileNotFoundException {
        List<DmpFileEntry> dmpFileEntries = new CsvToBeanBuilder<DmpFileEntry>(new FileReader(dmpSampleFilePath))
                .withType(DmpFileEntry.class)
                .build()
                .parse();

        return dmpFileEntries;
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
