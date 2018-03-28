package org.mkscc.igo.pi.dmptoigo.dmp.converter;

public interface BamPathRepository {
    void loadMappings();

    String getBamPathByBamId(String bamId);
}
