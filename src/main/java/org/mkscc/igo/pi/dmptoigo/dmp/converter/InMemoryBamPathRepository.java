package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.BamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryBamPathRepository implements BamPathRepository {
    private static final Logger LOGGER = LogManager.getLogger(InMemoryBamPathRepository.class);
    private final UniqueBamInfosResolver uniqueBamInfosResolver;
    private Map<String, String> bamIdToBamPath = new HashMap<>();

    @Value("${dmp.bam.mapping.file.path}")
    private String dmpBamMappingFilePath;

    @Autowired
    public InMemoryBamPathRepository(UniqueBamInfosResolver uniqueBamInfosResolver) {
        this.uniqueBamInfosResolver = uniqueBamInfosResolver;
    }

    @Override
    @PostConstruct
    public void loadMappings() {
        try {
            LOGGER.info(String.format("Loading anonymized bam ids to bam path mappings from file: %s",
                    dmpBamMappingFilePath));
            List<BamInfo> bamInfos = new ArrayList<>(new CsvToBeanBuilder<BamInfo>(new FileReader
                    (dmpBamMappingFilePath))
                    .withType(BamInfo.class)
                    .build()
                    .parse()
            );

            bamIdToBamPath = uniqueBamInfosResolver.resolve(bamInfos);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File with BAM path mapping not found: %s",
                    dmpBamMappingFilePath));
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
