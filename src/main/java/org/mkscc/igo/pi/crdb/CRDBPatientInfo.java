package org.mkscc.igo.pi.crdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CRDBPatientInfo {
    @JsonProperty("PRM_JOB_STATUS")
    private String jobStatus;

    @JsonProperty("PRM_PT_GENDER")
    private String gender;

    @JsonProperty("PRM_PT_ID")
    private String patientId;

    @JsonProperty("PRM_ERR_MSG")
    private String errorMessage;

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
