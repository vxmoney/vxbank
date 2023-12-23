package vxbank.datastore.data.utils;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.Date;
import java.util.UUID;

public class TestingUtils {


    public static VxUser generatePersistRandomUser(VxBankDatastore ds) {
        String randomString = UUID.randomUUID()
                .toString();
        String email = String.format("$%s@mail.com", randomString);

        VxUser vxUser = VxUser.builder()
                .email(email)
                .build();
        VxDsService.persist(vxUser, ds, VxUser.class);
        return vxUser;

    }


    public static VxEvent generatePersistEvent(VxBankDatastore ds, Long vxUserId, VxEvent.Type type, String currency,
                                               Long entryPrice) {
        String randomString = UUID.randomUUID()
                .toString();
        Long timeStamp = new Date().getTime();
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUserId)
                .type(type)
                .state(VxEvent.State.openForRegistration)
                .createTimeStamp(timeStamp)
                .currency(currency)
                .entryPrice(entryPrice)
                .build();
        VxDsService.persist(vxEvent,ds,VxEvent.class);
        return vxEvent;

    }
}
