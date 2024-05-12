package eu.vxbank.api.endpoints.publicevent.clinetpayment.dto;

import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;

import java.util.List;

public class PublicEventClientPaymentReportResponse {
    public Long vxPublicEventClientId;
    public String clientEmail;
    public Long vxPublicEventId;
    public Long availableBalance;
    public Long totalDebit;
    public Long totalCredit;
    public List<VxPublicEventClientPayment> clinetPaymentList;
}
