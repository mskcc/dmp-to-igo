package org.mkscc.igo.pi.dmptoigo.dmp;

public interface DmpSamplesRetriever {
    DmpPatientWithSamples retrieve(String dmpPatientId);
}
