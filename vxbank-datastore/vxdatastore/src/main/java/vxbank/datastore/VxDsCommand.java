package vxbank.datastore;

import com.googlecode.objectify.Objectify;
import vxbank.datastore.VxBankDatastore;

public abstract class VxDsCommand implements Runnable {


    private VxBankDatastore ds;

    public VxDsCommand(VxBankDatastore ds) {
        this.ds = ds;
    }

    public void execute() {
        ds.ofy.transact(this);
    }

    public Objectify ofy(){
        return ds.ofy;
    }
    public VxBankDatastore getDs() {
        return ds;
    }
}
