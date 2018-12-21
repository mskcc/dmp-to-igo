package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mskcc.util.notificator.Notificator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class BamAwareDmpFileEntryToSampleConverter implements DmpFileEntryToSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(BamAwareDmpFileEntryToSampleConverter.class);

    private static int foundCounter = 0;
    private static int notFoundCounter = 0;
    private BamPathRetriever bamPathRetriever;
    private Predicate<String> fileExistsPredicate;
    private Notificator notificator;

    @Autowired
    protected BamAwareDmpFileEntryToSampleConverter(
            BamPathRetriever bamPathRetriever,
            Predicate<String> fileExistsPredicate,
            Notificator notificator) {
        this.bamPathRetriever = bamPathRetriever;
        this.fileExistsPredicate = fileExistsPredicate;
        this.notificator = notificator;
    }

    @Override
    public DMPSample convert(DmpFileEntry dmpFileEntry) {
        String bamPath = bamPathRetriever.retrieveBamPath(dmpFileEntry.getAnnonymizedBamId());

        validateBamPathExists(bamPath);
        validateBaiPathExists(dmpFileEntry);

        DMPSample dmpSample = convertPart(dmpFileEntry, dmpFileEntry.getPatientId());
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

    private void validateBaiPathExists(DmpFileEntry dmpFileEntry) {
        String baiPath = bamPathRetriever.retrieveBaiPath(dmpFileEntry.getAnnonymizedBamId());

        if (!pathExists(baiPath)) {
            String message = String.format("Bai file: %s doesn't exist", baiPath);
            LOGGER.warn(message);
            tryToNotifyOfErrors(message, dmpFileEntry.getDmpSampleId());
        }
    }

    private void validateBamPathExists(String bamPath) {
        if (!pathExists(bamPath))
            throw new BamPathDoesntExistException(String.format("%d. Bam path doesn't exist: %s", notFoundCounter++, bamPath));
        throw new BamPathDoesntExistException(String.format("%d. Bam path exists: %s", foundCounter++, bamPath));
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

    private void tryToNotifyOfErrors(String message, String dmpSampleId) {
        try {
            notificator.notifyMessage("", message);
        } catch (Exception e) {
            LOGGER.warn(String.format("Unable to send notification about errors in converting dmp sample: %s",
                    dmpSampleId), e);
        }
    }
}
