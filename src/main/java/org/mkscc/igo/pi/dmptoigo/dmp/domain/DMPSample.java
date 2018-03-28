package org.mkscc.igo.pi.dmptoigo.dmp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMPSample implements Serializable {
    @JsonProperty("dmp_pool_name")
    private String runID;

    private String annonymizedRunID;

    private String bamPath;

    @JsonProperty("dmp_sample_id")
    private String dmpId;

    private DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();

    private String sampleType;

    public String getRunID() {
        return runID;
    }

    public void setRunID(String runID) {
        this.runID = runID;
    }

    public String getBamPath() {
        return bamPath;
    }

    public void setBamPath(String bamPath) {
        this.bamPath = bamPath;
    }

    public String getDmpId() {
        return dmpId;
    }

    public void setDmpId(String dmpId) {
        this.dmpId = dmpId;
    }

    public String getPatientDmpId() {
        return dmpSampleIdView.getPatientId();
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public int getCounter() {
        return dmpSampleIdView.getCounter();
    }

    public String getTumorNormal() {
        return dmpSampleIdView.getTumorNormal();
    }

    @Override
    public String toString() {
        return "DMPSample{" +
                "runID='" + runID + '\'' +
                ", dmpId='" + dmpId + '\'' +
                ", sampleType='" + sampleType + '\'' +
                '}';
    }

    public DMPSampleIdView getDmpSampleIdView() {
        return dmpSampleIdView;
    }

    public void setDmpSampleIdView(DMPSampleIdView dmpSampleIdView) {
        this.dmpSampleIdView = dmpSampleIdView;
    }

    public String getAnnonymizedRunID() {
        return annonymizedRunID;
    }

    public void setAnnonymizedRunID(String annonymizedRunID) {
        this.annonymizedRunID = annonymizedRunID;
    }
}
