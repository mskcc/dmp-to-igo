package org.mkscc.igo.pi.dmptoigo.cmo.repository;

import org.mskcc.domain.external.ExternalSample;

import java.util.List;

public interface ExternalSampleRepository {
    boolean exists(String sampleId);

    void save(ExternalSample externalSample);

    List<ExternalSample> getAll();
}
