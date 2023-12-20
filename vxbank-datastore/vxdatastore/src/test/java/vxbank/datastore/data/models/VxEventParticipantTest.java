package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxDsService;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class VxEventParticipantTest {

    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());


    @Test
    void testVxEventParticipant() {
        // user
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);

        // event
        Long createTimeStamp = new Date().getTime();
        Long entryPrice = 1000L; // 2 decimal denomination
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .createTimeStamp(createTimeStamp)
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
        Assertions.assertNotNull(vxEventParticipant.id);
    }

    @Test
    void testGetByEventId() {
        // user
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);

        // event
        Long createTimeStamp = new Date().getTime();
        Long entryPrice = 1000L; // 2 decimal denomination
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .createTimeStamp(createTimeStamp)
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

        // get by eventId
        List<VxEventParticipant> participantList = VxDsService.getListByEventId(ds,
                vxEvent.id,
                VxEventParticipant.class);
        Assertions.assertEquals(1, participantList.size());
    }

}
