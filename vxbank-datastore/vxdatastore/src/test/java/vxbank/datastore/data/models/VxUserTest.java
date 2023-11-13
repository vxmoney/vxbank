package vxbank.datastore.data.models;

import com.googlecode.objectify.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VxUserTest {

    @Test
    void testCreateGet(){
        VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

        VxUser vxUser = new VxUser();
        Key<VxUser> key = ds.ofy.save().entity(vxUser).now();
        Assertions.assertNotNull(key);
        Assertions.assertNotNull(vxUser.id);
    }
}