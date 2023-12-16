package vxbank.datastore.data.models;

import com.googlecode.objectify.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;

import java.util.Optional;


public class TestVxServiceIntegration {


    @Test
    void testCreateGet() {
        VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

        ExampleModel vxServiceIntegration = new ExampleModel();
        vxServiceIntegration.id = 1L;
        vxServiceIntegration.title = "chessout-integration-test";
        vxServiceIntegration.description = "Integration test description";
        Key<ExampleModel> key = ds.ofy.save().entity(vxServiceIntegration).now();
        Assertions.assertNotNull(key);

    }
}
