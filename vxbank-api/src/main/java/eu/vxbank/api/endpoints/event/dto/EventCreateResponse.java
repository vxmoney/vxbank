package eu.vxbank.api.endpoints.event.dto;

import lombok.Data;
import vxbank.datastore.data.models.VxEvent;

@Data
public class EventCreateResponse {
    public Long id;
    public Long vxUserId;
    public VxEvent.Type type;
    public VxEvent.State state;
    public String title;
    public Long createTimeStamp;
    public String currency;
    public Long entryPrice;
}
