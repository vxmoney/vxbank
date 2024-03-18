package eu.vxbank.api.endpoints.event.dto;

public class EventPayJoinResponse {
    public Long vxUserId;
    public Long vxEventId;
    public Long vxEventPaymentId;
    public String stripeSessionId;
    public String stripeSessionPaymentUrl;
}
