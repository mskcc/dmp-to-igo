package org.mkscc.igo.pi.dmptoigo.cmo.repository;

import org.mskcc.domain.external.ExternalSample;

public interface ExternalSampleRepository {
    boolean exists(String sampleId);

    void save(ExternalSample externalSample);
}
