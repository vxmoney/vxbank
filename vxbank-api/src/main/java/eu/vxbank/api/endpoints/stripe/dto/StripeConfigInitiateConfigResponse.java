package eu.vxbank.api.endpoints.stripe.dto;

import vxbank.datastore.data.models.VxStripeConfig;

public class StripeConfigInitiateConfigResponse {

    public Long userId;
    public String stripeAccountId;
    public VxStripeConfig.State state;
    public Long expiresAt;
    public String url;
    public Boolean configurationComplete;
}
