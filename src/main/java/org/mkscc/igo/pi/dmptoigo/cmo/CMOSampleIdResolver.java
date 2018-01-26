package org.mkscc.igo.pi.dmptoigo.cmo;

import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.sample.ExternalSample;

public interface CMOSampleIdResolver {
    String resolve(ExternalSample externalSample);
}
