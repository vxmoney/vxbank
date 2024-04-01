package eu.vxbank.api.endpoints.publicevent.clinetpayment.dto;

import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;

import java.util.List;

public class PublicEventClientPaymentReportResponse {
    public Long id;
    public Long userId;
    public Long publicEventId;
    public Long availableBalance;
    public Long totalDebit;
    public Long totalCredit;
    public List<VxPublicEventClientPayment> clinetPaymentList;
}
