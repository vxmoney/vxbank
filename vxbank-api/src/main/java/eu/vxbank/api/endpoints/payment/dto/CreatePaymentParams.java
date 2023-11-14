package eu.vxbank.api.endpoints.payment.dto;

public class CreatePaymentParams {
    public Long vxUserId;
    public Long vxServiceIntegrationId;
    public Long serviceIntegrationUserId;
    public String currency;
    public String productName;
    public Long valuePayedByUser;
    public Long valueAvailableToUser;
}
