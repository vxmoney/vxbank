package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxService;

import java.util.*;

public class VxIntegrationIntegrationTest {
    VxBankDatastore ds = VxBankDatastore.init("my-project", VxBankDatastore.ConnectionType.localhost, Optional.empty());

    private VxUser createUser() {
        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);
        vxUser = VxService.persist(vxUser, ds, VxUser.class);
        return vxUser;
    }

    private VxIntegration createIntegration(VxUser vxUser, VxIntegration.Type type) {
        VxIntegration integration = new VxIntegration();
        integration.userId = vxUser.id;
        integration.type = type;
        integration.title = "test-title";
        integration.description = "test-description";

        integration = VxService.persist(integration, ds, VxIntegration.class);
        return integration;
    }

    @Test
    void testPersistAndSearch() {

        VxUser user = createUser();

        Map<String, Object> filterList = new HashMap<>();

        List<VxIntegration> vxIntegrationList = VxService.getByUserId(user.id, filterList, ds, VxIntegration.class);

        Assertions.assertEquals(0, vxIntegrationList.size());

        VxIntegration integration = createIntegration(user, VxIntegration.Type.vxgaming);
        integration = createIntegration(user, VxIntegration.Type.chessout);


        // search with empty filter
        vxIntegrationList = VxService.getByUserId(user.id, filterList, ds, VxIntegration.class);
        Assertions.assertEquals(2, vxIntegrationList.size());

        // search with filter
        filterList.put("type", VxIntegration.Type.vxgaming);
        vxIntegrationList = VxService.getByUserId(user.id, filterList, ds, VxIntegration.class);
        Assertions.assertEquals(1, vxIntegrationList.size());

    }
}
