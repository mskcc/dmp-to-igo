package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.BamInfo;
import org.mskcc.util.notificator.Notificator;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryBamPathRepository implements BamPathRepository {
    private static final Logger LOGGER = LogManager.getLogger(InMemoryBamPathRepository.class);
    private static final FileSystem fs = FileSystems.getDefault();
    private Map<String, String> bamIdToBamPath = new HashMap<>();
    private String bamMappingFilePath;
    private Notificator notificator;

    public InMemoryBamPathRepository(String bamMappingFilePath, Notificator notificator) {
        this.bamMappingFilePath = bamMappingFilePath;
        this.notificator = notificator;
    }

    @Override
    @PostConstruct
    public void loadMappings() {
        try {
            LOGGER.info(String.format("Loading anonymized bam ids to bam path mappings from file: %s",
                    bamMappingFilePath));
            List<BamInfo> bamInfos = new ArrayList<>(new CsvToBeanBuilder<BamInfo>(new FileReader
                    (bamMappingFilePath))
                    .withType(BamInfo.class)
                    .build()
                    .parse()
            );

            removeAmbiguousMappings(bamInfos);

            bamIdToBamPath = bamInfos.stream()
                    .collect(Collectors.toMap(
                            BamInfo::getBamId,
                            BamInfo::getBamPath
                    ));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File with BAM path mapping not found: %s", bamMappingFilePath));
        }
    }


    private void removeAmbiguousMappings(List<BamInfo> bamInfos) {
        List<String> ambiguousBamIds = new ArrayList<>();

        for (BamInfo bamInfo : bamInfos) {
            String bamId = bamInfo.getBamId();
            if (ambiguousBamIds.contains(bamId))
                continue;

            if (bamIdToBamPath.containsKey(bamId)) {
                ambiguousBamIds.add(bamId);
                bamInfos.remove(bamId);

                String message = String.format("Multiple BAM paths found for bam id: %s. This bam id will be removed " +
                        "from cache.", bamId);
                LOGGER.warn(message);

                tryToNotify(message);
            }
        }
    }

    private void tryToNotify(String message) {
        try {
            notificator.notifyMessage("", message);
        } catch (Exception e) {
            LOGGER.warn(String.format("Unable to send notification: %s", message));
        }
    }

    @Override
    public String getBamPathByBamId(String bamId) {
        if (!bamIdToBamPath.containsKey(bamId))
            throw new NoBamPathFoundException(String.format("No BAM path found for bam id: %s", bamId));

        return bamIdToBamPath.get(bamId);
    }

    class NoBamPathFoundException extends RuntimeException {
        public NoBamPathFoundException(String message) {
            super(message);
        }
    }
}
