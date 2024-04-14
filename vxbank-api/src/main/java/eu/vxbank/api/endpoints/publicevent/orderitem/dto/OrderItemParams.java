package eu.vxbank.api.endpoints.publicevent.orderitem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemParams {
    public Long vxPublicEventProductId;
    public Long quantity;
    public Long value;
}
