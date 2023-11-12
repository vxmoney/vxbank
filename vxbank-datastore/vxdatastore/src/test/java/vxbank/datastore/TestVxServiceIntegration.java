package vxbank.datastore;

import com.googlecode.objectify.Key;
import org.junit.jupiter.api.Test;
import vxbank.datastore.data.models.VxDataTestEntity;
import vxbank.datastore.data.models.VxServiceIntegration;

import java.util.Optional;


public class TestVxServiceIntegration {


    @Test
    void testCreateGet() {
        VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

        VxServiceIntegration vxServiceIntegration = new VxServiceIntegration();
        vxServiceIntegration.id = 1L;
        vxServiceIntegration.title = "chessout-integration-test";
        vxServiceIntegration.description = "Integration test description";
        Key<VxServiceIntegration> key = ds.ofy.save().entity(vxServiceIntegration).now();

    }
}
