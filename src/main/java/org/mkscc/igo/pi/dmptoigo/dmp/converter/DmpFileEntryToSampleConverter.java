package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public abstract class DmpFileEntryToSampleConverter {
    @Autowired
    private BamPathRetriever bamPathRetriever;

    public DMPSample convert(DmpFileEntry dmpFileEntry, DMPSampleIdView dmpSampleIdView) {
        DMPSample dmpSample = convertPart(dmpFileEntry, dmpSampleIdView.getPatientId());

        dmpSample.setBamPath(bamPathRetriever.retrieve(dmpFileEntry.getAnnonymizedBamId()));
        dmpSample.setSampleType(getSampleClass(dmpFileEntry));
        dmpSample.setDmpSampleIdView(dmpSampleIdView);
        dmpSample.setAnnonymizedRunID(dmpFileEntry.getAnnonymizedProjectName());

        return dmpSample;
    }

    private String getSampleClass(DmpFileEntry dmpFileEntry) {
        if (Objects.equals(dmpFileEntry.getSampleType(), "-"))
            return "";
        return dmpFileEntry.getSampleType();
    }

    public abstract DMPSample convertPart(DmpFileEntry dmpFileEntry, String patientId);
}
