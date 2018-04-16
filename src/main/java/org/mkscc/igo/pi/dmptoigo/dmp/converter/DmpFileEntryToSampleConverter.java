package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;

public interface DmpFileEntryToSampleConverter {
    DMPSample convert(DmpFileEntry dmpFileEntry);
}
