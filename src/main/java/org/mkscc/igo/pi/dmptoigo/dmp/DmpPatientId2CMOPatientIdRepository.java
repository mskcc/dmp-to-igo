package org.mkscc.igo.pi.dmptoigo.dmp;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DmpPatientId2CMOPatientIdRepository extends HashMap<String, String> {
    public String getCMOPatientIdByDMPPatientId(String dmpPatientId) {
        return get(dmpPatientId);
    }
}
