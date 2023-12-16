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

    private ExampleModel createIntegration(VxUser vxUser, ExampleModel.Type type) {
        ExampleModel integration = new ExampleModel();
        integration.userId = vxUser.id;
        integration.type = type;
        integration.title = "test-title";
        integration.description = "test-description";

        integration = VxService.persist(integration, ds, ExampleModel.class);
        return integration;
    }

    @Test
    void testPersistAndSearch() {

        VxUser user = createUser();

        Map<String, Object> filterList = new HashMap<>();

        List<ExampleModel> vxIntegrationList = VxService.getByUserId(user.id, filterList, ds, ExampleModel.class);

        Assertions.assertEquals(0, vxIntegrationList.size());

        ExampleModel integration = createIntegration(user, ExampleModel.Type.vxgaming);
        integration = createIntegration(user, ExampleModel.Type.chessout);


        // search with empty filter
        vxIntegrationList = VxService.getByUserId(user.id, filterList, ds, ExampleModel.class);
        Assertions.assertEquals(2, vxIntegrationList.size());

        // search with filter
        filterList.put("type", ExampleModel.Type.vxgaming);
        vxIntegrationList = VxService.getByUserId(user.id, filterList, ds, ExampleModel.class);
        Assertions.assertEquals(1, vxIntegrationList.size());

    }
}
