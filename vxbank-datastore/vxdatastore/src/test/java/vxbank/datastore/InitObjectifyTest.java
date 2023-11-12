package vxbank.datastore;

import com.googlecode.objectify.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.data.models.VxDataTestEntity;
import vxbank.datastore.exceptions.VxBankDatastoreException;

import java.util.Optional;

class InitObjectifyTest {

    @Test
    void initObjectifyTest() throws VxBankDatastoreException {
        VxBankDatastore pd = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());
        Assertions.assertTrue(pd.getInitialized());

        VxDataTestEntity PDMyTestEntity = new VxDataTestEntity();
        PDMyTestEntity.objectId = "01";
        PDMyTestEntity.message = "Test message";
        Key<VxDataTestEntity> key = pd.ofy.save()
                .entity(PDMyTestEntity)
                .now();
        Assertions.assertNotNull(key);
    }
}