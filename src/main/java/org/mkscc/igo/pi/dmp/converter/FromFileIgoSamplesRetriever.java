package org.mkscc.igo.pi.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.log4j.Logger;
import org.mkscc.igo.pi.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class FromFileIgoSamplesRetriever implements IgoSamplesRetriever<DMPSample> {
    private static final Logger LOGGER = Logger.getLogger(FromFileIgoSamplesRetriever.class);
    private final DmpFileEntryToSampleConverter<DMPSample> dmpFileEntryToSampleConverter;

    @Value("${dmp.samples.file.path}")
    private String dmpSampleFilePath;

    @Autowired
    public FromFileIgoSamplesRetriever(DmpFileEntryToSampleConverter dmpFileEntryToSampleConverter) {
        this.dmpFileEntryToSampleConverter = dmpFileEntryToSampleConverter;
    }

    @Override
    public List<DMPSample> retrieve() {
        LOGGER.info(String.format("Retrieving samples from file: %s", dmpSampleFilePath));

        List<DMPSample> dmpSamples = new ArrayList<>();

        try {
            List<DmpFileEntry> dmpFileEntries = getDmpFileEntries(dmpSampleFilePath);
            for (DmpFileEntry dmpFileEntry : dmpFileEntries) {
                try {
                    DMPSample dmpSample = convert(dmpFileEntry);
                    dmpSamples.add(dmpSample);
                } catch (Exception e) {
                    LOGGER.warn(String.format("File Entry for sample: %s couldn't be converted to sample. It won't be" +
                            " processed", dmpFileEntry.getDmpSampleId()), e);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(String.format("File with DMP samples not found: %s. No samples will be saved.", dmpSampleFilePath)
                    , e);
        }

        return dmpSamples;
    }

    private List<DmpFileEntry> getDmpFileEntries(String dmpFilePath) throws FileNotFoundException {
        return new CsvToBeanBuilder<DmpFileEntry>(new FileReader(dmpFilePath))
                        .withType(DmpFileEntry.class)
                        .build()
                        .parse();
    }

    private DMPSample convert(DmpFileEntry dmpFileEntry) {
        return dmpFileEntryToSampleConverter.convert(dmpFileEntry);
    }
}
