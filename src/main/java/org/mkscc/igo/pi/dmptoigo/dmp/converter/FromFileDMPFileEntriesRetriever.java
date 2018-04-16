package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Component
public class FromFileDMPFileEntriesRetriever implements DMPFileEntriesRetriever {
    private static final Logger LOGGER = LogManager.getLogger(DMPSamplesGateway.class);

    @Value("${dmp.samples.file.path}")
    private String dmpSampleFilePath;

    @Override
    public List<DmpFileEntry> retrieve() {
        LOGGER.info(String.format("Retrieving samples from file: %s", dmpSampleFilePath));

        try {
            List<DmpFileEntry> dmpFileEntries = new CsvToBeanBuilder<DmpFileEntry>(new FileReader(dmpSampleFilePath))
                    .withType(DmpFileEntry.class)
                    .build()
                    .parse();

            return dmpFileEntries;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File with DMP samples not found: %s. No samples will be saved.",
                    dmpSampleFilePath), e);
        }
    }
}
