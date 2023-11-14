package eu.vxbank.api.endpoints.payment.dto;

public class CreatePaymentParams {
    public Long vxUserId;
    public Long vxServiceIntegrationId;
    public Long vxPaymentId;
    public String currency;
    public String productName;
    public Long valuePayedByUser;
    public Long valueAvailableToUser;
}
