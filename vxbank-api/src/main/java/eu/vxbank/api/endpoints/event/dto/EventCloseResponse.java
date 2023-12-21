package eu.vxbank.api.endpoints.event.dto;

import vxbank.datastore.data.models.VxEvent;

public class EventCloseResponse {

    public Long id;

    public Long vxUserId;

    public VxEvent.Type type;

    public VxEvent.State state;

    public String vxIntegrationId;

    public String title;
    public Long createTimeStamp;
    public String currency;
    public Long entryPrice;
}
