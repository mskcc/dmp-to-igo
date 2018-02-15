package org.mkscc.igo.pi.dmptoigo.cmo;

import org.mskcc.domain.sample.ExternalSample;

@FunctionalInterface
public interface CMOSampleIdResolver {
    String resolve(ExternalSample externalSample);
}
