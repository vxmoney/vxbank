package eu.vxbank.api.endpoints.user.dto;

import vxbank.datastore.data.models.VxStripeConfig;

import java.util.List;

public class LoginResponse {

    public Long id;
    public String email;
    public String name;
    public String message;
    public String vxToken;
    public Long vxTokenExpiresAt;
    public VxStripeConfig.State stripeConfigState;
    public List<Funds> availableFundsList;
    public String stripeId;


}
