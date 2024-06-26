package eu.vxbank.api.endpoints.event.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import vxbank.datastore.data.models.VxEvent;

@Data
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

    public static EventCloseResponse newInstance(VxEvent vxEvent) {
        ModelMapper mm = new ModelMapper();
        EventCloseResponse resposne = mm.map(vxEvent, EventCloseResponse.class);
        return resposne;
    }
}
