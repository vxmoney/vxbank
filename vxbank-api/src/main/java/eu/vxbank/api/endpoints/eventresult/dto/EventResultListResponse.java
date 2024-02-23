package eu.vxbank.api.endpoints.eventresult.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import vxbank.datastore.data.models.VxEventResult;

import java.util.List;

@Data
public class EventResultListResponse {
    public List<VxEventResult> eventResultList;

    public static EventResultListResponse newInstance(List<VxEventResult> eventResultList){
        EventResultListResponse response = new EventResultListResponse();
        response.eventResultList = eventResultList;
        return response;
    }
}
