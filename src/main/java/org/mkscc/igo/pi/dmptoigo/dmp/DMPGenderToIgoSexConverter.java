package org.mkscc.igo.pi.dmptoigo.dmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.RestCMOPatientInfoRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mskcc.domain.patient.Sex;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DMPGenderToIgoSexConverter {
    private static final Logger LOGGER = LogManager.getLogger(RestCMOPatientInfoRetriever.class);

    private static Map<DMPSample.Gender, Sex> dmpGender2IgoSex = new HashMap<>();

    static {
        dmpGender2IgoSex.put(DMPSample.Gender.MALE, Sex.M);
        dmpGender2IgoSex.put(DMPSample.Gender.FEMALE, Sex.F);
    }

    public Sex convert(DMPSample.Gender gender) {
        if (!dmpGender2IgoSex.containsKey(gender))
            throw new RuntimeException(String.format("DMP Gender %s is not supported", gender));

        return dmpGender2IgoSex.get(gender);
    }

}
