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

        FxIntegrationDeprecation vxServiceIntegration = new FxIntegrationDeprecation();
        vxServiceIntegration.id = 1L;
        vxServiceIntegration.title = "chessout-integration-test";
        vxServiceIntegration.description = "Integration test description";
        Key<FxIntegrationDeprecation> key = ds.ofy.save().entity(vxServiceIntegration).now();
        Assertions.assertNotNull(key);

    }
}
