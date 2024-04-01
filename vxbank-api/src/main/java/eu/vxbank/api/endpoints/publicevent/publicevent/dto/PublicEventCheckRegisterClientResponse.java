package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import lombok.Data;

@Data
public class PublicEventCheckRegisterClientResponse {
    public Long id;
    public Long userId;
    public Long publicEventId;
    public Long timeStamp;
}
