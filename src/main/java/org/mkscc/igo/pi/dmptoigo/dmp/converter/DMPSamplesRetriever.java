package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import java.util.List;

public interface DMPSamplesRetriever<T> {
    List<T> retrieve();
}
