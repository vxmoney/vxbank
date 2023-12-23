package eu.vxbank.api.endpoints.event.comands;

import eu.vxbank.api.endpoints.event.dto.EventCloseParams;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxDsCommand;

public class Close1v1EventCommand extends VxDsCommand {


    private Long currentUserId;
    private EventCloseParams params;


    public Close1v1EventCommand(VxBankDatastore ds, Long currentUserId, EventCloseParams params) {
        super(ds);
        this.currentUserId = currentUserId;
        this.params = params;
    }

    @Override
    public void run() {

        checkCurrentUserIsParticipant();

        throw new IllegalStateException("Please implement this");
    }

    private void checkCurrentUserIsParticipant() {
        throw new IllegalStateException("Please implement this");
    }
}
