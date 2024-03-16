package eu.vxbank.api.endpoints.event.dto;

import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import vxbank.datastore.data.models.VxEvent;

public class EventPayCreateResponse {
    public Long vxUserId;
    public Long vxEventId;
    public Long vxEventPaymentId;
    public String stripeSessionId;
    public String stripeSessionPaymentUrl;
}
