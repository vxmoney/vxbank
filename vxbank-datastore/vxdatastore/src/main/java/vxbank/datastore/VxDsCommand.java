package vxbank.datastore;

import vxbank.datastore.VxBankDatastore;

public abstract class VxDsCommand implements Runnable {
    private VxBankDatastore ds;

    public VxDsCommand(VxBankDatastore ds) {
        this.ds = ds;
    }

    public void execute() {
        ds.ofy.transact(this);
    }

    public void setDs(VxBankDatastore ds) {
        this.ds = ds;
    }
}
