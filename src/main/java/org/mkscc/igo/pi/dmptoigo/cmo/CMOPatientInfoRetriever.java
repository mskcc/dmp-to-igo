package org.mkscc.igo.pi.dmptoigo.cmo;

import org.mskcc.domain.patient.CRDBPatientInfo;

public interface CMOPatientInfoRetriever {
    CRDBPatientInfo resolve(String mrn);
}
