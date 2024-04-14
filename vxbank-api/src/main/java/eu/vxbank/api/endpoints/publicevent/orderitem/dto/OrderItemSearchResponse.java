package eu.vxbank.api.endpoints.publicevent.orderitem.dto;

import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;

import java.util.List;

public class OrderItemSearchResponse {
    public int totalCount;
    public List<VxPublicEventOrderItem> orderItemList;
}
