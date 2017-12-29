package org.mkscc.igo.pi.dmp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mkscc.igo.pi.dmp.domain.DMPSample;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DmpPatientWithSamples {
    @JsonProperty("mrn")
    private String mrn;

    @JsonProperty("dmp_patient_id")
    private String dmpPatientId;

    private List<DMPSample> samples = new ArrayList<>();

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

    public List<DMPSample> getSamples() {
        return samples;
    }

    public void setSamples(List<DMPSample> samples) {
        this.samples = samples;
    }
}
