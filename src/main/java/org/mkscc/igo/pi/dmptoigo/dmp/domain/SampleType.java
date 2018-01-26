package org.mkscc.igo.pi.dmptoigo.dmp.domain;

import org.mskcc.domain.sample.SampleClass;

import java.util.HashMap;
import java.util.Map;

public enum SampleType {
    PRIMARY("0", SampleClass.PRIMARY),
    METASTATIC("1", SampleClass.METASTASIS);

    private final String value;
    private final SampleClass sampleClass;

    private static Map<String, SampleType> valueToSampleType = new HashMap<>();

    static {
        for (SampleType sampleType : values()) {
            valueToSampleType.put(sampleType.getValue(), sampleType);
        }
    }

    SampleType(String value, SampleClass sampleClass) {

        this.value = value;
        this.sampleClass = sampleClass;
    }

    public static SampleType getByValue(String value) {
        if(!valueToSampleType.containsKey(value))
            throw new IllegalArgumentException(String.format("Unsupported Sample Type with value: %d", value));

        return valueToSampleType.get(value);
    }

    public String getValue() {
        return value;
    }

    public SampleClass getSampleClass() {
        return sampleClass;
    }
}
