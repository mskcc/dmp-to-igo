package org.mkscc.igo.pi.dmptoigo.dmp;

import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalRunIdRepository;
import org.mskcc.domain.external.ExternalRun;

import java.util.HashMap;

public class InMemoryExternalRunIdRepository extends HashMap<String, String> implements ExternalRunIdRepository {
    @Override
    public String getRunIdByAnonymizedRunId(String anonymizedRunId) {
        return get(anonymizedRunId);
    }

    @Override
    public void store(ExternalRun externalRun) {
        put(externalRun.getAnonymizedId(), externalRun.getRealRunId());
    }
}
