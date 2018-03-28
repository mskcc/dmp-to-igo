package org.mkscc.igo.pi.dmptoigo.cmo.repository;

import org.mskcc.domain.external.ExternalRun;

public interface ExternalRunIdRepository {
    String getRunIdByAnonymizedRunId(String anonymizedRunId);

    void store(ExternalRun externalRun);
}
