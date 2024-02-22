package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxDsService;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.*;

public class VxEventTest {

    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());

    @Test
    void testVxEvent(){
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);
        Assertions.assertNotNull(vxUser.id);
        Long createTimeStamp = new Date().getTime();
        Long entryPrice = 1000L; // 2 decimal denomination
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .createTimeStamp(createTimeStamp)
                .currency("eur")
                .entryPrice(entryPrice)
                .build();
        VxDsService.persist(vxEvent, ds, VxEvent.class);

        Assertions.assertNotNull(vxEvent.id);
    }

    @Test
    void testQueryEvent(){
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);
        Assertions.assertNotNull(vxUser.id);
        Long createTimeStamp = new Date().getTime();
        String uniqueServiceId = String.valueOf(createTimeStamp);
        Long entryPrice = 1000L; // 2 decimal denomination

        {
            VxEvent vxEventA = VxEvent.builder()
                    .vxUserId(vxUser.id)
                    .createTimeStamp(createTimeStamp)
                    .currency("eur")
                    .entryPrice(entryPrice)
                    .vxIntegrationId(uniqueServiceId)
                    .vxGame(VxGame.leagueOfLegends)
                    .state(VxEvent.State.inProgress)
                    .build();
            VxDsService.persist(vxEventA, ds, VxEvent.class);

        }
        {
            VxEvent vxEventB = VxEvent.builder()
                    .vxUserId(vxUser.id)
                    .createTimeStamp(createTimeStamp)
                    .currency("eur")
                    .entryPrice(entryPrice)
                    .vxIntegrationId(uniqueServiceId)
                    .vxGame(VxGame.leagueOfLegends)
                    .state(VxEvent.State.openForRegistration)
                    .build();
            VxDsService.persist(vxEventB, ds, VxEvent.class);

        }

        List<VxEvent.State> stateList = Arrays.asList(VxEvent.State.inProgress, VxEvent.State.openForRegistration);

        List<VxEvent> vxEventList = VxDsService.searchEvent(ds, uniqueServiceId,VxGame.leagueOfLegends, stateList);
        Assertions.assertEquals(2, vxEventList.size());
    }

}
