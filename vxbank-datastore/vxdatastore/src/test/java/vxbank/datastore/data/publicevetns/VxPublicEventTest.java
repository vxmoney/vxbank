package vxbank.datastore.data.publicevetns;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.Date;
import java.util.Optional;

public class VxPublicEventTest {
    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());



    @Test
    void createTest(){
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);
        Long createTimeStamp = new Date().getTime();
        VxPublicEvent vxPublicEvent = VxPublicEvent.builder()
                .vxUserId(vxUser.id)
                .title("test-title")
                .createTimeStamp(createTimeStamp)
                .build();
        VxDsService.persist(vxPublicEvent, ds, VxPublicEvent.class);
        Assertions.assertNotNull(vxPublicEvent.id);


    }

}
