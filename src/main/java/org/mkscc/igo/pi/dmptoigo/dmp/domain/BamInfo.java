package org.mkscc.igo.pi.dmptoigo.dmp.domain;

import com.opencsv.bean.CsvBindByPosition;

public class BamInfo {
    @CsvBindByPosition(position = 0)
    private String bamId;

    @CsvBindByPosition(position = 1)
    private String bamPath;

    @CsvBindByPosition(position = 2)
    private String group;

    public String getBamId() {
        return bamId;
    }

    public void setBamId(String bamId) {
        this.bamId = bamId;
    }

    public String getBamPath() {
        return bamPath;
    }

    public void setBamPath(String bamPath) {
        this.bamPath = bamPath;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
