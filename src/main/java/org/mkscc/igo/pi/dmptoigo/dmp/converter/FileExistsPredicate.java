package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Predicate;

@Component
@Qualifier("fileExistsPredicate")
public class FileExistsPredicate implements Predicate<String> {
    private static final Logger LOGGER = LogManager.getLogger(FileExistsPredicate.class);

    @Override
    public boolean test(String filePath) {
        LOGGER.info(String.format("Checking file path: %s for existence", filePath));

        return Files.exists(Paths.get(filePath));
    }
}
