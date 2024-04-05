package eu.vxbank.api.endpoints.publicevent.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vxbank.datastore.data.publicevent.VxPublicEventProduct;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductUpdateParams {
    public Long vxPublicEventId;
    public String title;
    public String description;
    public VxPublicEventProduct.Availability availability;
    public Long price;
}
