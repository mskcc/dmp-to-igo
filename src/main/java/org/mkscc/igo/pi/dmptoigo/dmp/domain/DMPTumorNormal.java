package org.mkscc.igo.pi.dmptoigo.dmp.domain;

import org.mskcc.domain.sample.TumorNormalType;

import java.util.HashMap;
import java.util.Map;

public enum DMPTumorNormal {
    TUMOR("T", TumorNormalType.TUMOR),
    NORMAL("N", TumorNormalType.NORMAL);

    private final String dmpValue;
    private final TumorNormalType igoValue;

    private static final Map<String, DMPTumorNormal> valueToDMPTumorNormal = new HashMap<>();

    static {
        for (DMPTumorNormal dmpTumorNormal : values()) {
            valueToDMPTumorNormal.put(dmpTumorNormal.getDmpValue(), dmpTumorNormal);
        }
    }

    DMPTumorNormal(String dmpValue, TumorNormalType igoValue) {

        this.dmpValue = dmpValue;
        this.igoValue = igoValue;
    }

    public String getDmpValue() {
        return dmpValue;
    }

    public TumorNormalType getIgoValue() {
        return igoValue;
    }

    public static DMPTumorNormal getByValue(String value) {
        if(!valueToDMPTumorNormal.containsKey(value))
            throw new IllegalArgumentException(String.format("DMP Tumor Normal for value: %s doesn't exist", value));

        return valueToDMPTumorNormal.get(value);
    }
}
