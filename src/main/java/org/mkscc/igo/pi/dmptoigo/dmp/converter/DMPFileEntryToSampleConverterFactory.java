package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.cmo.repository.ExternalRunIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPTumorNormal;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mkscc.igo.pi.external.rest.ServiceExternalRunIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DMPFileEntryToSampleConverterFactory {
    private static final Logger LOGGER = LogManager.getLogger(DMPFileEntryToSampleConverterFactory.class);

    @Autowired
    private BasicDMPFileEntryToSampleConverter basicDMPFileEntryToSampleConverter;

    @Autowired
    private CachingDMPFileEntryToSampleConverter cachingDMPFileEntryToSampleConverter;

    @Autowired
    private DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;

    @Autowired
    private ExternalRunIdRepository externalRunIdRepository;

    public DmpFileEntryToSampleConverter getConverter(DmpFileEntry dmpFileEntry) {
        if (shouldCache(dmpFileEntry)) {
            LOGGER.info(String.format("Using caching run ids converter for dmp file entry: %s", dmpFileEntry
                    .getDmpSampleId()));

            return cachingDMPFileEntryToSampleConverter;
        }

        return basicDMPFileEntryToSampleConverter;
    }

    private boolean shouldCache(DmpFileEntry dmpFileEntry) {
        return isTumor(dmpFileEntry.getTumorNormal()) && !valuesCached(dmpFileEntry);
    }

    private boolean valuesCached(DmpFileEntry dmpFileEntry) {
        return containsPatientMapping(dmpFileEntry) &&
                containsRunId(dmpFileEntry.getAnnonymizedProjectName());
    }

    private boolean containsPatientMapping(DmpFileEntry dmpFileEntry) {
        boolean containsPatient = dmpPatientId2CMOPatientIdRepository.containsKey(dmpFileEntry.getPatientId());

        if (!containsPatient)
            LOGGER.info(String.format("No mapping for patient dmp id: %s found in cache. Value will be retreived",
                    dmpFileEntry.getPatientId()));

        return containsPatient;
    }

    private boolean containsRunId(String anonymizedRunId) {
        try {
            return !StringUtils.isEmpty(externalRunIdRepository.getRunIdByAnonymizedRunId(anonymizedRunId));
        } catch (ServiceExternalRunIdRepository.ExternalRunNotFoundException e) {
            return false;
        }
    }

    private boolean isTumor(String tumorNormal) {
        return Objects.equals(tumorNormal, DMPTumorNormal.TUMOR.getDmpValue());
    }
}
