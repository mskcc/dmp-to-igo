package org.mkscc.igo.pi.dmp.domain;

import com.opencsv.bean.CsvBindByPosition;

public class DmpFileEntry {
    @CsvBindByPosition(position = 0)
    private String dmpSampleId;

    @CsvBindByPosition(position = 1)
    private String annonymizedBamId;

    @CsvBindByPosition(position = 2)
    private String groupId;

    @CsvBindByPosition(position = 3)
    private String partCconsentStatus;

    @CsvBindByPosition(position = 4)
    private String oncotreeCode;

    @CsvBindByPosition(position = 5)
    private String sampleType;

    @CsvBindByPosition(position = 6)
    private String primarySite;

    @CsvBindByPosition(position = 7)
    private String metastasisSite;

    @CsvBindByPosition(position = 8)
    private String tissueType;

    @CsvBindByPosition(position = 9)
    private String annonymizedProjectName;

    @CsvBindByPosition(position = 10)
    private String sampleCoverage;

    @CsvBindByPosition(position = 11)
    private String somaticCallingStatus;

    @CsvBindByPosition(position = 12)
    private String majorAlleleContamination;

    @CsvBindByPosition(position = 13)
    private String minorAlleleContamination;

    public String getDmpSampleId() {
        return dmpSampleId;
    }

    public void setDmpSampleId(String dmpSampleId) {
        this.dmpSampleId = dmpSampleId;
    }

    public String getAnnonymizedBamId() {
        return annonymizedBamId;
    }

    public void setAnnonymizedBamId(String annonymizedBamId) {
        this.annonymizedBamId = annonymizedBamId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPartCconsentStatus() {
        return partCconsentStatus;
    }

    public void setPartCconsentStatus(String partCconsentStatus) {
        this.partCconsentStatus = partCconsentStatus;
    }

    public String getOncotreeCode() {
        return oncotreeCode;
    }

    public void setOncotreeCode(String oncotreeCode) {
        this.oncotreeCode = oncotreeCode;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getPrimarySite() {
        return primarySite;
    }

    public void setPrimarySite(String primarySite) {
        this.primarySite = primarySite;
    }

    public String getTissueType() {
        return tissueType;
    }

    public void setTissueType(String tissueType) {
        this.tissueType = tissueType;
    }

    public String getMetastasisSite() {
        return metastasisSite;
    }

    public void setMetastasisSite(String metastasisSite) {
        this.metastasisSite = metastasisSite;
    }

    public String getAnnonymizedProjectName() {
        return annonymizedProjectName;
    }

    public void setAnnonymizedProjectName(String annonymizedProjectName) {
        this.annonymizedProjectName = annonymizedProjectName;
    }

    public String getSampleCoverage() {
        return sampleCoverage;
    }

    public void setSampleCoverage(String sampleCoverage) {
        this.sampleCoverage = sampleCoverage;
    }

    public String getSomaticCallingStatus() {
        return somaticCallingStatus;
    }

    public void setSomaticCallingStatus(String somaticCallingStatus) {
        this.somaticCallingStatus = somaticCallingStatus;
    }

    public String getMajorAlleleContamination() {
        return majorAlleleContamination;
    }

    public void setMajorAlleleContamination(String majorAlleleContamination) {
        this.majorAlleleContamination = majorAlleleContamination;
    }

    public String getMinorAlleleContamination() {
        return minorAlleleContamination;
    }

    public void setMinorAlleleContamination(String minorAlleleContamination) {
        this.minorAlleleContamination = minorAlleleContamination;
    }
}
