package org.mkscc.igo.pi.dmptoigo.dmp.domain;

public class DMPSampleIdView {
    private String patientId = "";
    private String tumorNormal = "";
    private int counter;
    private String assay = "";

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTumorNormal() {
        return tumorNormal;
    }

    public void setTumorNormal(String tumorNormal) {
        this.tumorNormal = tumorNormal;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getAssay() {
        return assay;
    }

    public void setAssay(String assay) {
        this.assay = assay;
    }

    @Override
    public String toString() {
        return "DMPSampleIdView{" +
                "patientId='" + patientId + '\'' +
                ", tumorNormal='" + tumorNormal + '\'' +
                ", counter='" + counter + '\'' +
                ", assay='" + assay + '\'' +
                '}';
    }
}
