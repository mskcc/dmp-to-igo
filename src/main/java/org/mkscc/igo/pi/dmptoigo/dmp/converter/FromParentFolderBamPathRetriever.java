package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FromParentFolderBamPathRetriever implements BamPathRetriever {
    private static final Logger LOGGER = LogManager.getLogger(FromParentFolderBamPathRetriever.class);
    public static final String BAM_FILE_EXTENTION = "bam";
    public static final String BAI_FILE_EXTENTION = "bai";

    private final String bamParentFolder;

    public FromParentFolderBamPathRetriever(String bamParentFolder) {
        this.bamParentFolder = bamParentFolder;
    }

    @Override
    public String retrieveBamPath(String anonymizedBamId) {
        LOGGER.info(String.format("Resolving bam path for anonymized bam id: %s", anonymizedBamId));

        String igoBamPath = String.format("%s%s.%s", bamParentFolder, anonymizedBamId, BAM_FILE_EXTENTION);

        return igoBamPath;
    }

    @Override
    public String retrieveBaiPath(String annonymizedBamId) {
        LOGGER.info(String.format("Resolving bai path for anonymized bam id: %s", annonymizedBamId));

        String igoBaiPath = String.format("%s%s.%s", bamParentFolder, annonymizedBamId, BAI_FILE_EXTENTION);

        return igoBaiPath;
    }
}
