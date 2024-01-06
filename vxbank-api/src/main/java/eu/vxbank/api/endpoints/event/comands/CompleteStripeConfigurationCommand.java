package eu.vxbank.api.endpoints.event.comands;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxDsCommand;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.service.VxDsService;

public class CompleteStripeConfigurationCommand extends VxDsCommand {

    private Long currentUserId;
    private VxStripeConfig vxStripeConfig;

    public CompleteStripeConfigurationCommand(VxBankDatastore ds, Long currentUserId) {
        super(ds);
        this.currentUserId = currentUserId;
    }

    @Override
    public void run() {

        vxStripeConfig = VxDsService.getByUserId(VxStripeConfig.class, getDs(), currentUserId)
                .get(0);
        vxStripeConfig.state = VxStripeConfig.State.active;
        VxDsService.persist(vxStripeConfig, getDs(), VxStripeConfig.class);
    }


}
