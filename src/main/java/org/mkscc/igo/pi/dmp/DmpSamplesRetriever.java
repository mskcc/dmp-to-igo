package org.mkscc.igo.pi.dmp;

public interface DmpSamplesRetriever {
    DmpPatientWithSamples retrieve(String dmpPatientId);
}
