package org.mkscc.igo.pi.cmo;

import org.mkscc.igo.pi.dmp.domain.DMPSample;

public interface CMOSampleIdResolver {
    String resolve(DMPSample dmpSample);
}
