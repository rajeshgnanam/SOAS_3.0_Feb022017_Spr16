package org.kuali.ole.deliver.bo;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import java.sql.Timestamp;


/**
 * Created by govindarajank on 9/10/17.
 */
public class BatchJobLastRunBo extends PersistableBusinessObjectBase {

    private Integer id;
    private Timestamp lastRunTIme;

    public Timestamp getLastRunTIme() {
        return lastRunTIme;
    }

    public void setLastRunTIme(Timestamp lastRunTIme) {
        this.lastRunTIme = lastRunTIme;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
