package org.mkscc.igo.pi.dmp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mskcc.domain.sample.NucleicAcid;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.SampleOrigin;
import org.mskcc.domain.sample.SpecimenType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DMPSample extends Sample {

    // @TODO confirm defualt values with PMs
    public static final SampleOrigin DEFAULT_SAMPLE_ORIGIN = SampleOrigin.WHOLE_BLOOD;
    public static final NucleicAcid DEFAULT_NUCLEID_ACID = NucleicAcid.DNA;
    public static final SpecimenType DEFAULT_SPECIMEN_TYPE = SpecimenType.BLOOD;
    private String runID;
    private String bamPath;

    @JsonCreator
    public DMPSample(@JsonProperty("dmp_sample_id") String id) {
        super(id);
    }

    @JsonProperty("dmp_pool_name")
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
}
