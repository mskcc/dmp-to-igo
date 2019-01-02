package org.mkscc.igo.pi.dmptoigo.dmp.converter;

public interface BamPathRetriever {
    String retrieveBamPath(String annonymizedBamId);
    String retrieveBaiPath(String annonymizedBamId);
}
