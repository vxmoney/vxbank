package eu.vxbank.api.endpoints.stripe.dto;

import vxbank.datastore.data.models.VxStripeConfig;

public class StripeConfigFinalizeConfigResponse {
    public Long userId;
    public String stripeAccountId;
    public VxStripeConfig.State state;

}
