package eu.vxbank.api.endpoints.event.comands;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxDsCommand;

public class Close1v1EventCommand extends VxDsCommand {

    private VxBankDatastore ds;


    public Close1v1EventCommand(VxBankDatastore ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        throw new IllegalStateException("Please implement this");
    }
}
