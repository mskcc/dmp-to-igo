package org.mkscc.igo.pi.dmptoigo.dmp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DmpPatient {
    @JsonProperty("mrn")
    private String mrn;

    @JsonProperty("dmp_patient_id")
    private String dmpPatientId;

    private Map<String, DMPSample> samples = new HashMap<>();

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getDmpPatientId() {
        return dmpPatientId;
    }

    public void setDmpPatientId(String dmpPatientId) {
        this.dmpPatientId = dmpPatientId;
    }

    public Map<String, DMPSample> getSamples() {
        return samples;
    }

    public void setSamples(Map<String, DMPSample> samples) {
        this.samples = samples;
    }
}
