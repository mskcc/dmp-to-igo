package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.BamInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
class FromFileBamPathRetriever implements BamPathRetriever {

    @Value("${dmp.bam.mapping.file.path}")
    private String bamMappingFilePath;

    @Override
    public String retrieve(String annonymizedBamId) {
        try {
            List<BamInfo> bamInfos = new CsvToBeanBuilder<BamInfo>(new FileReader(bamMappingFilePath))
                    .withType(BamInfo.class)
                    .build()
                    .parse()
                    .stream()
                    .filter(b -> b.getBamId().equals(annonymizedBamId))
                    .collect(Collectors.toList());

            validateBamPathExists(annonymizedBamId, bamInfos);

            String dmpBamPath = bamInfos.get(0).getBamPath();
            String igoBamPath = String.format("/ifs%s", dmpBamPath);

            return igoBamPath;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File with BAM path mapping not found: %s", bamMappingFilePath));
        }
    }

    private void validateBamPathExists(String annonymizedBamId, List<BamInfo> foundBamInfos) {
        if (foundBamInfos.size() == 0)
            throw new NoBamPathFoundException(String.format("No BAM path found for bam id: %s", annonymizedBamId));
        if (foundBamInfos.size() > 1)
            throw new AmbiguousBamPathException(String.format("Multiple BAM paths found for bam id: %s",
                    annonymizedBamId));
    }

    private class NoBamPathFoundException extends RuntimeException {
        public NoBamPathFoundException(String message) {
            super(message);
        }
    }

    private class AmbiguousBamPathException extends RuntimeException {
        public AmbiguousBamPathException(String message) {
            super(message);
        }
    }
}
