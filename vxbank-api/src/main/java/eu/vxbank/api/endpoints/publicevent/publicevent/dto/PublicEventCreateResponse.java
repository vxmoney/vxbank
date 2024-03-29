package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import lombok.Data;
import java.util.List;

@Data
public class PublicEventCreateResponse {
    public Long id;
    public Long vxUserId;
    public List<Long> managerIdList;
    public String vxIntegrationId;
    public String title;
    public String currency;
    public Long createTimeStamp;
}
