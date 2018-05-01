package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasicDMPFileEntryToSampleConverter extends BamAwareDmpFileEntryToSampleConverter {
    private static final Logger LOGGER = LogManager.getLogger(BasicDMPFileEntryToSampleConverter.class);

    @Autowired
    public BasicDMPFileEntryToSampleConverter(BamPathRetriever bamPathRetriever) {
        super(bamPathRetriever);
    }

    @Override
    public DMPSample convertPart(DmpFileEntry dmpFileEntry, String patientId) {
        DMPSample dmpSample = new DMPSample();
        dmpSample.setDmpId(dmpFileEntry.getDmpSampleId());

        LOGGER.info(String.format("Converted dmp file entry: %s to dmp sample", dmpSample));

        return dmpSample;
    }
}
