package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public abstract class BamAwareDmpFileEntryToSampleConverter implements DmpFileEntryToSampleConverter {
    private BamPathRetriever bamPathRetriever;

    @Autowired
    protected BamAwareDmpFileEntryToSampleConverter(BamPathRetriever bamPathRetriever) {
        this.bamPathRetriever = bamPathRetriever;
    }

    @Override
    public DMPSample convert(DmpFileEntry dmpFileEntry) {
        DMPSample dmpSample = convertPart(dmpFileEntry, dmpFileEntry.getPatientId());

        String bamPath = bamPathRetriever.retrieve(dmpFileEntry.getAnnonymizedBamId());
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

    private String getSampleClass(DmpFileEntry dmpFileEntry) {
        if (Objects.equals(dmpFileEntry.getSampleType(), "-"))
            return "";
        return dmpFileEntry.getSampleType();
    }

    public abstract DMPSample convertPart(DmpFileEntry dmpFileEntry, String patientId);
}
