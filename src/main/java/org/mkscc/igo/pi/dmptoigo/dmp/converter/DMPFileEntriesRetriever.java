package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;

import java.util.List;

public interface DMPFileEntriesRetriever {
    List<DmpFileEntry> retrieve();
}
