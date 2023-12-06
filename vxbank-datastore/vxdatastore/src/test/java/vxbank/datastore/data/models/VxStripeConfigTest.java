package vxbank.datastore.data.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.service.VxService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VxStripeConfigTest {
    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());

    private VxUser persistRandomUser() {

        VxUser vxUser = new VxUser();
        String uuid = UUID.randomUUID()
                .toString();
        vxUser.email = String.format("$%s@mail.com", uuid);
        VxUser persistedUser = VxService.persist(vxUser, ds, VxUser.class);

        return persistedUser;
    }


    private VxStripeConfig persistStripeConfig(Long userId) {
        VxStripeConfig config = VxStripeConfig.builder()
                .userId(userId)
                .state(VxStripeConfig.State.notConfigured)
                .build();
        VxStripeConfig persistedConfig = VxService.persist(config, ds, VxStripeConfig.class);
        return persistedConfig;
    }


    @Test
    void testCreateAndGetByUserId() {
        VxUser vxUser = persistRandomUser();

        List<VxStripeConfig> emptyList = VxService.getByUserId(vxUser.id, new HashMap<>(), ds, VxStripeConfig.class);
        Assertions.assertEquals(0, emptyList.size());

        VxStripeConfig stripeConfig = persistStripeConfig(vxUser.id);
        Assertions.assertNotNull(stripeConfig.id);
        Assertions.assertEquals(vxUser.id, stripeConfig.userId);

        List<VxStripeConfig> configuredList = VxService.getByUserId(vxUser.id,
                new HashMap<>(),
                ds,
                VxStripeConfig.class);
        Assertions.assertEquals(1, configuredList.size());
        VxStripeConfig config = configuredList.get(0);
        Assertions.assertNotNull(config.id);
        Assertions.assertEquals(vxUser.id, config.userId);
    }

}
