package eu.vxbank.api.endpoints.eventresult.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import vxbank.datastore.data.models.VxEventResult;

@Data
public class EventResultCreateResponse {
    public Long id;
    public Long vxUserId;
    public Long vxEventId;
    public Long createTimeStamp;
    public Long updateTimeStamp;
    public Long participantId;
    public VxEventResult.FinalResultPlace participantFinalResultPlace;
    public Long prizeValue;

    public static EventResultCreateResponse newInstance(VxEventResult vxEventResult){
        ModelMapper mm = new ModelMapper();
        EventResultCreateResponse resposne = mm.map(vxEventResult, EventResultCreateResponse.class);
        return resposne;
    }
}
