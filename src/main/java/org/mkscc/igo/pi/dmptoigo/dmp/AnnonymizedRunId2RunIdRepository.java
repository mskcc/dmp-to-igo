package org.mkscc.igo.pi.dmptoigo.dmp;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AnnonymizedRunId2RunIdRepository extends HashMap<String, String> {
    public String getRunIdByAnnonymizedRunId(String annonymizedRunId) {
        return get(annonymizedRunId);
    }
}
