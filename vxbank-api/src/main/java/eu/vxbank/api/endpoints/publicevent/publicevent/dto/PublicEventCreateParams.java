package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublicEventCreateParams {
    public Long vxUserId;
    public VxIntegrationId vxIntegrationId;
    public String title;
    public String currency;
}
