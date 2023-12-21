package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxDsService;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public class VxEventResultTest {

    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());

    @Test
    void testVxEventParticipant() {
        // user
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);

        // event
        Long timeStamp = new Date().getTime();
        Long entryPrice = 1000L; // 2 decimal denomination
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .createTimeStamp(timeStamp)
                .currency("eur")
                .entryPrice(entryPrice)
                .build();
        VxDsService.persist(vxEvent, ds, VxEvent.class);

        // event participant
        VxEventParticipant vxEventParticipant = VxEventParticipant.builder()
                .vxUserId(vxUser.id)
                .vxEventId(vxEvent.id)
                .state(VxEventParticipant.State.active)
                .build();
        VxDsService.persist(vxEventParticipant, ds, VxEventParticipant.class);

        // event result
        VxEventResult vxEventResult = VxEventResult.builder()
                .vxUserId(vxUser.id)
                .vxEventId(vxEvent.id)
                .createTimeStamp(timeStamp)
                .participantId(vxUser.id)
                .participantFinalResultPlace(VxEventResult.FinalResultPlace.firstPlace)
                .prizeValue(800L)
                .build();
        VxDsService.persist(vxEventResult, ds, VxEventResult.class);
        Assertions.assertNotNull(vxEventResult.id);

        //get by eventId
        List<VxEventResult> resultList = VxDsService.getListByEventId(ds,
                vxEvent.id,
                VxEventResult.class);
        Assertions.assertEquals(1, resultList.size());
    }
}
