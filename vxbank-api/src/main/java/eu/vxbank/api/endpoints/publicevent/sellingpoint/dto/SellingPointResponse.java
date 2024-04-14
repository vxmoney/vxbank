package eu.vxbank.api.endpoints.publicevent.sellingpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellingPointResponse {
    public Long id;
    public Long vxPublicEventId;
    public String title;
    public List<VxPublicEventProduct> productList;
}
