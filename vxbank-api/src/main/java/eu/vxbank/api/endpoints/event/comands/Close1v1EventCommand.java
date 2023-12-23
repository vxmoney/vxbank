package eu.vxbank.api.endpoints.event.comands;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxDsCommand;

public class Close1v1EventCommand extends VxDsCommand {



    public Close1v1EventCommand(VxBankDatastore ds){
        super(ds);
    }

    @Override
    public void run() {
        throw new IllegalStateException("Please implement this");
    }
}
