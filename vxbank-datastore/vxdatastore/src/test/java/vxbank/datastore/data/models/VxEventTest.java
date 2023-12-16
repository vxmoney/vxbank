package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.Optional;

public class VxEventTest {

    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());

    @Test
    void testVxEvent(){
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);
        Assertions.assertNotNull(vxUser.id);
    }

}
