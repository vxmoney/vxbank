package eu.vxbank.api.sidehelpers;

import org.junit.jupiter.api.Assertions;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.HashMap;

public class SideStripeConfigHelper {
    public static void setStripeAccountId(VxBankDatastore ds, Long vxUserId, String newStripeAccountId) {
        VxStripeConfig vxStripeConfig = VxDsService.getByUserId(vxUserId, new HashMap<>(), ds, VxStripeConfig.class)
                .get(0);
        vxStripeConfig.state = VxStripeConfig.State.active;
        vxStripeConfig.stripeAccountId = newStripeAccountId;
        vxStripeConfig.currency = "eur";
        VxDsService.persist(vxStripeConfig, ds, VxStripeConfig.class);
    }


}
