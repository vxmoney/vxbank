package eu.vxbank.api.endpoints.publicevent.clinetpayment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerRegistersPaymentParams {
    public Long eventId;
    public Long clientId;
    public Long value;
}
