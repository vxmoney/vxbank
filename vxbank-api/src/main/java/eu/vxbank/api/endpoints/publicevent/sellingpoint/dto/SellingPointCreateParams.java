package eu.vxbank.api.endpoints.publicevent.sellingpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellingPointCreateParams {
    public Long vxPublicEventId;
    public String title;
    public List<Long> productIdList;
}
