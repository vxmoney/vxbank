package eu.vxbank.api.endpoints.event.comands;

import eu.vxbank.api.endpoints.event.dto.EventCloseParams;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxDsCommand;
import vxbank.datastore.data.models.VxEventParticipant;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;
import java.util.Optional;

public class Close1v1EventCommand extends VxDsCommand {


    private Long currentUserId;
    private EventCloseParams params;
    private List<VxEventParticipant> participantList;


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

        VxDsService.transactionLess(getDs(), () ->{
            participantList = VxDsService
                    .getListByEventId(VxEventParticipant.class, getDs(), params.vxEventId);

            if (!userIsParticipant(currentUserId,participantList)){
                throw new IllegalStateException("You are not a participant. You are not allowed to close this event");
            }
        });
    }

    private boolean userIsParticipant(Long vxUserId , List<VxEventParticipant> list) {
        Optional<VxEventParticipant> optionalParticipant = list.stream()
                .filter(p -> p.vxUserId.equals(vxUserId) && p.state.equals(VxEventParticipant.State.active))
                .findFirst();
        return optionalParticipant.isPresent();
    }
}
