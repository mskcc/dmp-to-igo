package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FromParentFolderBamPathRetriever implements BamPathRetriever {
    private static final Logger LOGGER = LogManager.getLogger(FromParentFolderBamPathRetriever.class);

    private final String bamParentFolder;

    public FromParentFolderBamPathRetriever(String bamParentFolder) {
        this.bamParentFolder = bamParentFolder;
    }

    @Override
    public String retrieve(String anonymizedBamId) {
        LOGGER.info(String.format("Resolving bam path for anonymized bam id: %s", anonymizedBamId));

        String igoBamPath = String.format("%s%s.bam", bamParentFolder, anonymizedBamId);

        return igoBamPath;
    }
}
