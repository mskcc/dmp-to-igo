package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.mkscc.igo.pi.dmptoigo.dmp.AnnonymizedRunId2RunIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPTumorNormal;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DMPFileEntryToSampleConverterFactory {
    @Autowired
    private BasicDMPFileEntryToSampleConverter basicDMPFileEntryToSampleConverter;

    @Autowired
    private CachingDMPFileEntryToSampleConverter cachingDMPFileEntryToSampleConverter;

    @Autowired
    private DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;

    @Autowired
    private AnnonymizedRunId2RunIdRepository annonymizedRunId2RunIdRepository;

    public DmpFileEntryToSampleConverter getConverter(DmpFileEntry dmpFileEntry) {
        if (shouldCache(dmpFileEntry))
            return cachingDMPFileEntryToSampleConverter;
        return basicDMPFileEntryToSampleConverter;
    }

    private boolean shouldCache(DmpFileEntry dmpFileEntry) {
        return isTumor(dmpFileEntry.getTumorNormal()) && !valuesCached(dmpFileEntry);
    }

    private boolean valuesCached(DmpFileEntry dmpFileEntry) {
        return dmpPatientId2CMOPatientIdRepository.containsKey(dmpFileEntry.getPatientId()) &&
                annonymizedRunId2RunIdRepository
                        .containsKey(dmpFileEntry.getAnnonymizedProjectName());
    }

    private boolean isTumor(String tumorNormal) {
        return Objects.equals(tumorNormal, DMPTumorNormal.TUMOR.getDmpValue());
    }
}
