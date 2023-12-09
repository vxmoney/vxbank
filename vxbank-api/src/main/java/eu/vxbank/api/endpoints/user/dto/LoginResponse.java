package eu.vxbank.api.endpoints.user.dto;

import vxbank.datastore.data.models.VxStripeConfig;

public class LoginResponse {

    public Long id;
    public String email;
    public String message;
    public String vxToken;
    public VxStripeConfig.State stripeConfigState;
}
