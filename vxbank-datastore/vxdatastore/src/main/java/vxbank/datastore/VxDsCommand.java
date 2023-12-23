package vxbank.datastore;

import vxbank.datastore.VxBankDatastore;

public abstract class  VxDsCommand implements Runnable{
   private VxBankDatastore ds;
   public void execute(){
       ds.ofy.transact(this);
   }
}
