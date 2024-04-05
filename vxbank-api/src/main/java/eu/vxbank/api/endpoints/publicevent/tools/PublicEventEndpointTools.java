package eu.vxbank.api.endpoints.publicevent.tools;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.publicevent.VxPublicEventManager;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PublicEventEndpointTools {
    public static void checkUserIsManagerOfEvent(VxBankDatastore ds, VxUser vxUser, Long vxPublicEventId){
        List<VxPublicEventManager> managerList = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                ds,
                vxPublicEventId);
        Set<Long> managersSet = managerList.stream().map(m -> m.userId).collect(Collectors.toSet());
        if (!managersSet.contains(vxUser.id)) {
            throw new IllegalStateException("User is not a manager for this event");
        }
    }

    public static void checkUserIsOwnerOfEvent(VxUser vxUser, VxPublicEvent vxPublicEvent) {
        if (vxPublicEvent.vxUserId != vxUser.id) {
            throw new IllegalStateException("User is not Owner of event");
        }
    }

    public static VxPublicEvent getVxEvent(VxBankDatastore ds, Long vxPublicEventId) {
        VxPublicEvent vxPublicEvent = VxDsService.getById(VxPublicEvent.class, ds, vxPublicEventId);
        return vxPublicEvent;
    }
}
