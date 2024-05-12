package eu.vxbank.api.endpoints.publicevent.clinetpayment;

import com.stripe.exception.StripeException;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentParams;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.ManagerRegistersPaymentResponse;
import eu.vxbank.api.endpoints.publicevent.clinetpayment.dto.PublicEventClientPaymentReportResponse;
import eu.vxbank.api.endpoints.publicevent.orderitem.dto.OrderItemParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsParams;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventClientDepositFundsResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventGetManagerListResponse;
import eu.vxbank.api.endpoints.publicevent.tools.PublicEventEndpointTools;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEventClient;
import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;
import vxbank.datastore.data.publicevent.VxPublicEventManager;
import vxbank.datastore.data.publicevent.VxPublicEventOrderItem;
import vxbank.datastore.data.service.VxDsService;

import java.util.ArrayList;
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
    public PublicEventClientPaymentReportResponse getClientReport(Authentication auth,
                                                                  @PathVariable Long eventId,
                                                                  @PathVariable Long clientId) {

        VxUser currentUser = systemService.validateAndGetUser(auth);

        VxPublicEventClient client = VxDsService.getById(VxPublicEventClient.class,
                systemService.getVxBankDatastore(),
                clientId);
        if (!eventId.equals(client.publicEventId)) {
            throw new IllegalStateException("Client does not belong to event");
        }

        checkUserCanViewClientPayments(currentUser, client);

        PublicEventClientPaymentReportResponse report = buildReportForClient(eventId, clientId);

        return report;
    }

    private PublicEventClientPaymentReportResponse buildReportForClient(Long eventId, Long clientId) {
        // payment list
        List<VxPublicEventClientPayment> paymentList =
                VxDsService.vxPublicEventClientId(VxPublicEventClientPayment.class,
                systemService.getVxBankDatastore(),
                clientId);
        paymentList = paymentList.stream()
                .filter(p -> p.state.equals(VxPublicEventClientPayment.State.complete))
                .filter(p -> p.vxPublicEventId.equals(eventId))
                .collect(Collectors.toList());

        // user email
        VxPublicEventClient client = VxDsService.getById(VxPublicEventClient.class,
                systemService.getVxBankDatastore(),
                clientId);
        String clientEmail = VxDsService.getById(VxUser.class, systemService.getVxBankDatastore(), client.userId).email;


        PublicEventClientPaymentReportResponse report = new PublicEventClientPaymentReportResponse();
        report.clientEmail = clientEmail;
        report.clinetPaymentList = paymentList;
        report.vxPublicEventId = eventId;
        report.vxPublicEventClientId = clientId;
        report.totalDebit = 0L;
        report.totalCredit = 0L;
        for (VxPublicEventClientPayment payment : paymentList) {
            if (payment.type == VxPublicEventClientPayment.Type.debit) {
                report.totalDebit += payment.value;
            }
            if (payment.type == VxPublicEventClientPayment.Type.credit) {
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
                systemService.getVxBankDatastore(),
                client.publicEventId);
        Optional<VxPublicEventManager> optionalManager = managers.stream()
                .filter(m -> m.userId.equals(currentUser.id))
                .findFirst();
        if (optionalManager.isPresent()) {
            return;
        }
        throw new IllegalStateException("User not allowed to vie client payments");
    }

    @PostMapping("/managerRegistersPayment")
    public ManagerRegistersPaymentResponse managerRegistersPayment(Authentication auth,
                                                                   @RequestBody ManagerRegistersPaymentParams params) throws
            StripeException {

        // checking security
        VxUser vxUser = systemService.validateAndGetUser(auth);
        PublicEventEndpointTools.checkUserIsManagerOfEvent(systemService.getVxBankDatastore(), vxUser, params.eventId);
        VxPublicEventClient client = VxDsService.getById(VxPublicEventClient.class,
                systemService.getVxBankDatastore(),
                params.clientId);
        if (!params.eventId.equals(client.publicEventId)) {
            throw new IllegalStateException("Client does not belong to event");
        }
        PublicEventClientPaymentReportResponse clientReport = buildReportForClient(params.eventId, params.clientId);
        if (clientReport.availableBalance < params.value) {
            throw new IllegalStateException("Client does not have sufficient funds");
        }

        Long timeStamp = System.currentTimeMillis();

        // register payment
        VxPublicEventClientPayment payment = VxPublicEventClientPayment.builder()
                .vxIntegrationId(VxIntegrationId.vxEvents.toString())
                .vxPublicEventId(params.eventId)
                .vxPublicEventClientId(params.clientId)
                .vxPublicEventManagerUserId(vxUser.id)
                .type(VxPublicEventClientPayment.Type.credit)
                .state(VxPublicEventClientPayment.State.complete)
                .method(VxPublicEventClientPayment.Method.managerRegistersPayment)
                .value(params.value)
                .timeStamp(timeStamp)
                .updatedTimeStamp(timeStamp)
                .build();
        VxDsService.persist(VxPublicEventClientPayment.class, systemService.getVxBankDatastore(), payment);

        ManagerRegistersPaymentResponse response = new ManagerRegistersPaymentResponse();
        response.publicEventClientPayment = payment;
        response.updatedAvailableBalance = clientReport.availableBalance - params.value;

        // if items are provided then we need to update the order items
        if (params.orderItemParamsList != null) {
            response.publicEventOrderItemList = new ArrayList<>();
            for (OrderItemParams itemParams : params.orderItemParamsList) {
                VxPublicEventOrderItem.VxPublicEventOrderItemBuilder itemBuilder = VxPublicEventOrderItem.builder()
                        .vxPublicClientPaymentId(payment.id)
                        .vxPublicEventId(params.eventId)
                        .vxPublicEventClientId(params.clientId)
                        .vxPublicEventManagerUserId(vxUser.id)
                        .vxPublicEventSellingPointId(params.vxPublicEventSellingPointId)
                        .vxPublicEventProductId(itemParams.vxPublicEventProductId)
                        .quantity(itemParams.quantity)
                        .value(itemParams.value)
                        .timeStamp(timeStamp);
                VxPublicEventOrderItem item = itemBuilder.build();
                VxDsService.persist(VxPublicEventOrderItem.class, systemService.getVxBankDatastore(), item);
                response.publicEventOrderItemList.add(item);
            }
        }

        return response;
    }

}
