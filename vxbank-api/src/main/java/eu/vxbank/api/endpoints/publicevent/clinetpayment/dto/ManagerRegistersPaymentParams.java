package eu.vxbank.api.endpoints.publicevent.clinetpayment.dto;


import eu.vxbank.api.endpoints.publicevent.orderitem.dto.OrderItemParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerRegistersPaymentParams {
    public Long eventId;
    public Long clientId;
    public Long value;
    public Long vxPublicEventSellingPointId;
    public List<OrderItemParams> orderItemParamsList;
}
