package eu.vxbank.api.endpoints.event.dto;
import vxbank.datastore.data.models.VxEvent.Type;



public class EventCreateParams {
    public Long vxUserId;
    public Type type;
    public String currency;
    public Long entryPrice;
}
