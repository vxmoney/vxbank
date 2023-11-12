package vxbank.datastore;

import org.junit.jupiter.api.Test;

import java.util.Optional;


public class TestVxServiceIntegration {

    @Test
    void testCreateGet(){
        VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());
    }
}
