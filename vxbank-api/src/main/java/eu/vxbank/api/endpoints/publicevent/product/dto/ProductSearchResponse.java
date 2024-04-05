package eu.vxbank.api.endpoints.publicevent.product.dto;

import vxbank.datastore.data.publicevent.VxPublicEventProduct;

import java.util.List;

public class ProductSearchResponse {
    public int totalCount;
    public List<VxPublicEventProduct> productList;
}
