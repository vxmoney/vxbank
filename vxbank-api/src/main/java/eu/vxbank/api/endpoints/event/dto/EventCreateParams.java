package eu.vxbank.api.endpoints.event.dto;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vxbank.datastore.data.models.VxEvent.Type;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventCreateParams {
    public Long vxUserId;
    public Type type;
    public VxIntegrationId vxIntegrationId;
    public String title;
    public String currency;
    public Long entryPrice;
}
