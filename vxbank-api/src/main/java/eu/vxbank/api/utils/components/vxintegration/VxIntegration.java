package eu.vxbank.api.utils.components.vxintegration;

import lombok.Data;

@Data
public class VxIntegration {
    public VxIntegrationId vxIntegrationId;
    public Long vxUserId;
    public String vxStripeId;
    public Long vxBankPercentage;
}
