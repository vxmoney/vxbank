package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublicEventAddMangerParams {
    public Long publicEventId;
    public String email;
}
