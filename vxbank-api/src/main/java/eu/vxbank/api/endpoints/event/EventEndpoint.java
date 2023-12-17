package eu.vxbank.api.endpoints.event;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import eu.vxbank.api.endpoints.event.dto.EventCreateParams;
import eu.vxbank.api.endpoints.event.dto.EventCreateResponse;
import eu.vxbank.api.endpoints.event.dto.EventGetResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxEventPayment;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/event")
public class EventEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @PostMapping
    public EventCreateResponse create(Authentication auth, @RequestBody EventCreateParams params) throws
            StripeException {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        if (!Objects.equals(vxUser.id, params.vxUserId)) {
            throw new IllegalStateException("You can not create events for someone else");
        }

        VxStripeConfig vxStripeConfig = VxDsService.getByUserId(vxUser.id,
                        new HashMap<>(),
                        systemService.getVxBankDatastore(),
                        VxStripeConfig.class)
                .get(0);


        Charge charge = VxStripeUtil.chargeConnectedAccount(stripeKeys.stripeSecretKey,
                vxStripeConfig.stripeAccountId,
                params.entryPrice,
                params.currency);

        Long createTimeStamp = new Date().getTime();

        // create event
        VxEvent vxEvent = VxEvent.builder()
                .vxUserId(vxUser.id)
                .state(VxEvent.State.openForRegistration)
                .type(params.type)
                .vxIntegrationId(params.vxIntegrationId.toString())
                .title(params.title)
                .currency(params.currency)
                .entryPrice(params.entryPrice)
                .createTimeStamp(createTimeStamp)
                .build();
        VxDsService.persist(vxEvent, systemService.getVxBankDatastore(), VxEvent.class);

        // create event payment

        VxEventPayment vxEventPayment = VxEventPayment.builder()
                .vxEventId(vxEvent.id)
                .vxUserId(vxUser.id)
                .type(VxEventPayment.Type.credit)
                .state(VxEventPayment.State.complete)
                .description("Event seed funds added by event creator: stripeChargeId" + charge.getId())
                .value(params.entryPrice)
                .build();
        VxDsService.persist(vxEventPayment, systemService.getVxBankDatastore(), VxEventPayment.class);

        ModelMapper mm = new ModelMapper();
        EventCreateResponse response = mm.map(vxEvent, EventCreateResponse.class);

        return response;
    }

    @GetMapping("/{eventId}")
    @ResponseBody
    public EventGetResponse get( @PathVariable Long eventId) {

        VxEvent vxEvent = VxDsService.getById(eventId, systemService.getVxBankDatastore(),VxEvent.class);
        if (vxEvent == null){
            throw new IllegalStateException("Not able to locate event by id = " + eventId);
        }

        ModelMapper mm = new ModelMapper();
        EventGetResponse response = mm.map(vxEvent, EventGetResponse.class);

        List<VxEventPayment> payments =
                VxDsService.getVxEventPaymentList(systemService.getVxBankDatastore(),eventId);

        Long totalCredit = payments.stream()
                .filter(payment -> payment.type == VxEventPayment.Type.credit &&
                        payment.state == VxEventPayment.State.complete )
                .mapToLong(VxEventPayment::getValue).sum();
        Long totalDebit = payments.stream()
                .filter(payment -> payment.type == VxEventPayment.Type.debit &&
                        payment.state == VxEventPayment.State.complete )
                .mapToLong(VxEventPayment::getValue).sum();

        Long availableFunds = totalCredit - totalDebit;
        response.availableFunds = availableFunds;



        return response;

    }

}
