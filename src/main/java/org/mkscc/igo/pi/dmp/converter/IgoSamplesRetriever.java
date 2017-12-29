package org.mkscc.igo.pi.dmp.converter;

import java.util.List;

public interface IgoSamplesRetriever<T> {
    List<T> retrieve();
}
