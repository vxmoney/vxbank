package eu.vxbank.api.endpoints.eventresult.dto;

import lombok.Data;
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
}
