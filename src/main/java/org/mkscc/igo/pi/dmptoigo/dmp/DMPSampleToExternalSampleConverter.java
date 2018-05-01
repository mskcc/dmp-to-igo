package org.mkscc.igo.pi.dmptoigo.dmp;

import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.external.ExternalSample;

public interface DMPSampleToExternalSampleConverter {
    ExternalSample convert(DMPSample dmpSample);
}
