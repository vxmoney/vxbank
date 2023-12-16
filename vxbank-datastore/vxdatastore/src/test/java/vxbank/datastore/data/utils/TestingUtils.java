package vxbank.datastore.data.utils;

import org.junit.jupiter.api.Assertions;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

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


}
