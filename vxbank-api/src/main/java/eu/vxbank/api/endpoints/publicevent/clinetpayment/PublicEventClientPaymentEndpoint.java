package eu.vxbank.api.endpoints.publicevent.clinetpayment;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentParams;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentResponse;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.PublicEventClientPaymentReportResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventGetManagerListResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEventClient;
import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;
import vxbank.datastore.data.publicevent.VxPublicEventManager;
import vxbank.datastore.data.service.VxDsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publicEventClientPayment")
public class PublicEventClientPaymentEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @GetMapping("/getClientReport/event/{eventId}/client/{clientId}")
    @ResponseBody
    public PublicEventClientPaymentReportResponse getClientReport(Authentication auth, @PathVariable Long eventId, @PathVariable Long clientId) {

        VxUser currentUser = systemService.validateAndGetUser(auth);

        VxPublicEventClient client = VxDsService.getById(VxPublicEventClient.class, systemService.getVxBankDatastore(), clientId);
        if (!eventId.equals(client.publicEventId)) {
            throw new IllegalStateException("Client does not belong to event");
        }

        checkUserCanViewClientPayments(currentUser, client);

        PublicEventClientPaymentReportResponse report = buildReportForClient(eventId, clientId);

        return report;
    }

    private PublicEventClientPaymentReportResponse buildReportForClient(Long eventId, Long clientId) {
        List<VxPublicEventClientPayment> paymentList = VxDsService.vxPublicEventClientId(VxPublicEventClientPayment.class,
                systemService.getVxBankDatastore(), clientId);
        paymentList = paymentList.stream().filter(p -> p.state.equals(VxPublicEventClientPayment.State.complete))
                .filter(p -> p.vxPublicEventId.equals(eventId))
                .collect(Collectors.toList());
        PublicEventClientPaymentReportResponse report = new PublicEventClientPaymentReportResponse();
        report.clinetPaymentList = paymentList;
        report.vxPublicEventId = eventId;
        report.vxPublicEventClientId = clientId;
        report.totalDebit = 0L;
        report.totalCredit = 0L;
        for (VxPublicEventClientPayment payment: paymentList){
            if (payment.type == VxPublicEventClientPayment.Type.debit){
                report.totalDebit += payment.value;
            }
            if (payment.type == VxPublicEventClientPayment.Type.credit){
                report.totalCredit += payment.value;
            }
        }
        report.availableBalance = report.totalDebit - report.totalCredit;
        return report;
    }

    private void checkUserCanViewClientPayments(VxUser currentUser, VxPublicEventClient client) {
        if (client.userId.equals(currentUser.id)) {
            return;
        }
        List<VxPublicEventManager> managers = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(), client.publicEventId);
        Optional<VxPublicEventManager> optionalManager = managers.stream().filter(
                m -> m.userId.equals(currentUser.id)).findFirst();
        if (optionalManager.isPresent()) {
            return;
        }
        throw new IllegalStateException("User not allowed to vie client payments");
    }

    @PostMapping("/managerRegistersPayment")
    public ManagerRegistersPaymentResponse managerRegistersPayment(Authentication auth,
                                                              @RequestBody ManagerRegistersPaymentParams params) throws
            StripeException {

        VxUser vxUser = systemService.validateAndGetUser(auth);
        throw new IllegalStateException("Please implement this");
    }

}
