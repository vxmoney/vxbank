package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxDsService;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.Date;
import java.util.Optional;

public class VxEventParticipantTest {

    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());


    @Test
    void testVxEventParticipant(){
        // user
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);

        // event
        Long createTimeStamp = new Date().getTime();
        Long entryPrice = 1000L; // 2 decimal denomination
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .createTimeStamp(createTimeStamp)
                .currency("eur")
                .entryPrice(entryPrice).build();
        VxDsService.persist(vxEvent, ds, VxEvent.class);

        // event participant

    }
}
