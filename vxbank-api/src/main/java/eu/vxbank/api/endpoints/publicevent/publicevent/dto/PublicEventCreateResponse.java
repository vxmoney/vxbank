package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import lombok.Data;

@Data
public class PublicEventCreateResponse {
    public Long id;
    public Long vxUserId;
    public String vxIntegrationId;
    public String title;
    public String currency;
    public Long createTimeStamp;
}
