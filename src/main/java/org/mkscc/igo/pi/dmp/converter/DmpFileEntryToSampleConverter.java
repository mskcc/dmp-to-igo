package org.mkscc.igo.pi.dmp.converter;

import org.mkscc.igo.pi.dmp.domain.DmpFileEntry;

public interface DmpFileEntryToSampleConverter<T> {
    T convert(DmpFileEntry dmpFileEntry);
}
