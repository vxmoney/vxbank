package eu.vxbank.api.endpoints.event.dto;

import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import vxbank.datastore.data.models.VxEvent;

public class EventPayCreateResponse {
    public Long id;
    public Long vxUserId;
    public VxEvent.Type type;
    public VxEvent.State state;
    public VxIntegrationId vxIntegrationId;
    public String title;
    public Long createTimeStamp;
    public String currency;
    public Long entryPrice;
}
