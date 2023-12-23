package vxbank.datastore.data.commands.close1v1event;

import vxbank.datastore.VxBankDatastore;

public abstract class  VxDsCommand implements Runnable{
   private VxBankDatastore ds;
   private void execute(){
       ds.ofy.transact(this);
   }
}
