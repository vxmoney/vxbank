package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublicEventAddManagerResponse {

    public Long id; // Public event manger table id
    public Long userId;
    public String email;
    public Long publicEventId;
    public Long timeStamp;
}
