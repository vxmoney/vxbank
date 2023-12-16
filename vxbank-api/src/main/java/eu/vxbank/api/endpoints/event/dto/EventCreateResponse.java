package eu.vxbank.api.endpoints.event.dto;

import vxbank.datastore.data.models.VxEvent;

public class EventCreateResponse {
    public Long id;

    public Long vxUserId;

    public VxEvent.Type type;
    public Long createTimeStamp;
    public String currency;
    public Long entryPrice;
}
