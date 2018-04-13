package org.mkscc.igo.pi.dmptoigo.dmp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

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
    private String oncotreeCode;
    private String primarySite;
    private String metastatisSite;
    private String tissueType;
    private Gender gender;

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
                ", annonymizedRunID='" + annonymizedRunID + '\'' +
                ", dmpId='" + dmpId + '\'' +
                ", dmpSampleIdView=" + dmpSampleIdView +
                ", sampleType='" + sampleType + '\'' +
                ", oncotreeCode='" + oncotreeCode + '\'' +
                ", primarySite='" + primarySite + '\'' +
                ", metastatisSite='" + metastatisSite + '\'' +
                ", tissueType='" + tissueType + '\'' +
                ", gender=" + gender +
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

    public String getOncotreeCode() {
        return oncotreeCode;
    }

    public void setOncotreeCode(String oncotreeCode) {
        this.oncotreeCode = oncotreeCode;
    }

    public String getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(String primarySite) {
        this.primarySite = primarySite;
    }

    public String getMetastatisSite() {
        return metastatisSite;
    }

    public void setMetastatisSite(String metastatisSite) {
        this.metastatisSite = metastatisSite;
    }

    public String getTissueType() {
        return tissueType;
    }

    public void setTissueType(String tissueType) {
        this.tissueType = tissueType;
    }

    public boolean isTumor() {
        return DMPTumorNormal.getByValue(getTumorNormal()) == DMPTumorNormal.TUMOR;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public enum Gender {
        MALE("Male"),
        FEMALE("Female");

        private static final Map<String, Gender> nameToEnum = new HashMap<>();

        static {
            for (Gender enumValue : values()) {
                nameToEnum.put(enumValue.name, enumValue);
            }
        }

        private final String name;

        Gender(String name) {
            this.name = name;
        }

        public static Gender fromString(String name) {
            if (!nameToEnum.containsKey(name))
                throw new RuntimeException(format("Unsupported %s: %s", Gender.class.getName(), name));

            return nameToEnum.get(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
