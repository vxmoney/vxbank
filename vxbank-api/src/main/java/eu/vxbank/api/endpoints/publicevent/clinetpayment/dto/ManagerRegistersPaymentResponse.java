package eu.vxbank.api.endpoints.publicevent.clinetpayment.dto;

import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;
import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;

import java.util.List;

public class ManagerRegistersPaymentResponse {

    public Long updatedAvailableBalance;
    public VxPublicEventClientPayment publicEventClientPayment;
    public List<VxPublicEventOrderItem> publicEventOrderItemList;
}
