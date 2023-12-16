package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxDsService;

import java.util.*;

public class VxIntegrationIntegrationTest {
    VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

    private VxUser createUser() {
        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);
        vxUser = VxDsService.persist(vxUser, ds, VxUser.class);
        return vxUser;
    }

    private VxExampleModel createIntegration(VxUser vxUser) {
        VxExampleModel integration = new VxExampleModel();
        integration.userId = vxUser.id;
        integration.title = "test-title";
        integration.description = "test-description";

        integration = VxDsService.persist(integration, ds, VxExampleModel.class);
        return integration;
    }

    @Test
    void testPersistAndSearch() {

        VxUser user = createUser();

        Map<String, Object> filterList = new HashMap<>();

        List<VxExampleModel> vxIntegrationList = VxDsService.getByUserId(user.id, filterList, ds, VxExampleModel.class);

        Assertions.assertEquals(0, vxIntegrationList.size());

        VxExampleModel integration = createIntegration(user);
        integration = createIntegration(user);


        // search with empty filter
        vxIntegrationList = VxDsService.getByUserId(user.id, filterList, ds, VxExampleModel.class);
        Assertions.assertEquals(2, vxIntegrationList.size());

        // search with filter was disabled because we removed type

    }
}
