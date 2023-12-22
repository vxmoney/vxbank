package eu.vxbank.api.endpoints.eventresult.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxEventResult;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventResultCreateParams {
    public Long vxUserId;
    public Long vxEventId;
    public Long createTimeStamp;
    public Long updateTimeStamp;
    public Long participantId;
    public VxEventResult.FinalResultPlace participantFinalResultPlace;
    public Long prizeValue;

    public VxEventResult buildVxEventResult(){

        ModelMapper mm = new ModelMapper();
        VxEventResult eventResult = mm.map(this, VxEventResult.class);
        eventResult.id = null;
        return eventResult;
    }
}
