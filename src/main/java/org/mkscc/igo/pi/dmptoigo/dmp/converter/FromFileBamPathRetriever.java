package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class FromFileBamPathRetriever implements BamPathRetriever {
    private static final Logger LOGGER = LogManager.getLogger(FromFileBamPathRetriever.class);

    private static final String BAM_PARENT_FOLDER = "/ifs";

    private BamPathRepository bamPathRepository;

    @Autowired
    public FromFileBamPathRetriever(BamPathRepository bamPathRepository) {
        this.bamPathRepository = bamPathRepository;
    }

    @Override
    public String retrieve(String anonymizedBamId) {
        LOGGER.info(String.format("Resolving bam path for anonymized bam id: %s", anonymizedBamId));

        String dmpBamPath = bamPathRepository.getBamPathByBamId(anonymizedBamId);
        String igoBamPath = String.format("%s%s", BAM_PARENT_FOLDER, dmpBamPath);

        return igoBamPath;
    }
}
