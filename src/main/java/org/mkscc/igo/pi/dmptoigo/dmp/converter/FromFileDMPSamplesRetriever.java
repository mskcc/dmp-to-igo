package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.AnnonymizedRunId2RunIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DMPSampleToExternalSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mskcc.domain.sample.ExternalSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Value("${external.sample.rest.url}")
    private String externalSampleRestUrl;

    @Value("${external.sample.rest.samples.endpoint}")
    private String samplesEndpoint;

    @Autowired
    @Qualifier("externalSampleRest")
    private RestTemplate restTemplate;

    @Autowired
    private DMPFileEntryToSampleConverterFactory dmpFileEntryToSampleConverterFactory;

    @Autowired
    private AnnonymizedRunId2RunIdRepository annonymizedRunId2RunIdRepository;

    @Override
    public List<DMPSample> retrieve() {
        LOGGER.info(String.format("Retrieving samples from file: %s", dmpSampleFilePath));

        try {
            List<DmpFileEntry> dmpFileEntries = getDmpFileEntries();

            List<DMPSample> dmpSamples = new ArrayList<>();
            for (DmpFileEntry dmpFileEntry : dmpFileEntries) {
                String dmpSampleId = dmpFileEntry.getDmpSampleId();
                try {
                    if (exists(dmpSampleId)) {
                        LOGGER.info(String.format("Sample with id %s already exists in database. It will be skipped.",
                                dmpSampleId));
                        continue;
                    }

                    dmpFileEntry.setDmpSampleIdView(retrieveDmpSampleIdView(dmpSampleId));

                    DMPSample dmpSample = convert(dmpFileEntry);
                    dmpSamples.add(dmpSample);
                } catch (Exception e) {
                    LOGGER.warn(String.format("File Entry for sample: %s couldn't be converted to sample. It won't be" +
                            " saved", dmpSampleId), e);
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

    private void fillRunIds(List<DMPSample> dmpSamples) {
        LOGGER.info(String.format("Filling in mapped run ids"));
        for (DMPSample dmpSample : dmpSamples) {
            if (StringUtils.isEmpty(dmpSample.getRunID())) {
                try {
                    String runId = annonymizedRunId2RunIdRepository.getRunIdByAnnonymizedRunId(dmpSample
                            .getAnnonymizedRunID());

                    LOGGER.debug(String.format("Filling in run id for dmp sample: %s with value: %s", dmpSample.getDmpId
                            (), runId));

                    dmpSample.setRunID(runId);
                } catch (Exception e) {
                    LOGGER.warn(String.format("Unable to fill in run id for dmp sample: %s", dmpSample), e);
                }
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

    private boolean exists(String sampleId) {
        String url = String.format("%s/%s/%s", externalSampleRestUrl, samplesEndpoint, sampleId);

        ResponseEntity<ExternalSample[]> externalSampleResponse = restTemplate.getForEntity(url,
                ExternalSample[].class);

        boolean sampleExists = externalSampleResponse.getStatusCode() == HttpStatus.NOT_FOUND ||
                externalSampleResponse.getBody()
                        .length > 0;

        return sampleExists;
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
