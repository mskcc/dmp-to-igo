package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class BamAwareDmpFileEntryToSampleConverter implements DmpFileEntryToSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(BamAwareDmpFileEntryToSampleConverter.class);

    private BamPathRetriever bamPathRetriever;
    private Predicate<String> fileExistsPredicate;

    @Autowired
    protected BamAwareDmpFileEntryToSampleConverter(
            BamPathRetriever bamPathRetriever,
            Predicate<String> fileExistsPredicate) {
        this.bamPathRetriever = bamPathRetriever;
        this.fileExistsPredicate = fileExistsPredicate;
    }

    @Override
    public DMPSample convert(DmpFileEntry dmpFileEntry) {
        DMPSample dmpSample = convertPart(dmpFileEntry, dmpFileEntry.getPatientId());

        String bamPath = bamPathRetriever.retrieve(dmpFileEntry.getAnnonymizedBamId());

        if (!pathExists(bamPath))
            throw new BamPathDoesntExistException(String.format("Bam path %s doesn't exists", bamPath));

        dmpSample.setBamPath(bamPath);
        dmpSample.setSampleType(getSampleClass(dmpFileEntry));
        dmpSample.setDmpSampleIdView(dmpFileEntry.getDmpSampleIdView());
        dmpSample.setAnnonymizedRunID(dmpFileEntry.getAnnonymizedProjectName());
        dmpSample.setOncotreeCode(dmpFileEntry.getOncotreeCode());
        dmpSample.setPrimarySite(dmpFileEntry.getPrimarySite());
        dmpSample.setMetastatisSite(dmpFileEntry.getMetastasisSite());
        dmpSample.setTissueType(dmpFileEntry.getTissueType());

        return dmpSample;
    }

    private boolean pathExists(String path) {
        return fileExistsPredicate.test(path);
    }

    private String getSampleClass(DmpFileEntry dmpFileEntry) {
        if (Objects.equals(dmpFileEntry.getSampleType(), "-"))
            return "";
        return dmpFileEntry.getSampleType();
    }

    public abstract DMPSample convertPart(DmpFileEntry dmpFileEntry, String patientId);

    public class BamPathDoesntExistException extends RuntimeException {
        public BamPathDoesntExistException(String message) {
            super(message);
        }
    }


}
