package eu.vxbank.api.endpoints.event;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import eu.vxbank.api.endpoints.event.dto.*;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.*;
import vxbank.datastore.data.service.VxDsService;

import java.util.*;
import java.util.stream.Collectors;

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

        // create participant
        VxEventParticipant vxEventParticipant = VxEventParticipant.builder()
                .vxUserId(vxUser.id)
                .vxEventId(vxEvent.id)
                .state(VxEventParticipant.State.active)
                .build();
        VxDsService.persist(vxEventParticipant, systemService.getVxBankDatastore(), VxEventParticipant.class);


        ModelMapper mm = new ModelMapper();
        EventCreateResponse response = mm.map(vxEvent, EventCreateResponse.class);

        return response;
    }


    @PostMapping("/join")
    public EventJoinResponse join(Authentication auth, @RequestBody EventJoinParams params) throws StripeException {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        if (!Objects.equals(vxUser.id, params.vxUserId)) {
            throw new IllegalStateException("You can not join events for someone else");
        }

        VxEvent vxEvent = VxDsService.getById(params.eventId, systemService.getVxBankDatastore(), VxEvent.class);

        VxStripeConfig vxStripeConfig = VxDsService.getByUserId(vxUser.id,
                        new HashMap<>(),
                        systemService.getVxBankDatastore(),
                        VxStripeConfig.class)
                .get(0);


        Charge charge = VxStripeUtil.chargeConnectedAccount(stripeKeys.stripeSecretKey,
                vxStripeConfig.stripeAccountId,
                vxEvent.entryPrice,
                vxEvent.currency);

        Long createTimeStamp = new Date().getTime();


        // create event payment
        VxEventPayment vxEventPayment = VxEventPayment.builder()
                .vxEventId(vxEvent.id)
                .vxUserId(vxUser.id)
                .type(VxEventPayment.Type.credit)
                .state(VxEventPayment.State.complete)
                .description("Event join: stripeChargeId" + charge.getId())
                .value(vxEvent.entryPrice)
                .build();
        VxDsService.persist(vxEventPayment, systemService.getVxBankDatastore(), VxEventPayment.class);

        // create participant
        VxEventParticipant vxEventParticipant = VxEventParticipant.builder()
                .vxUserId(vxUser.id)
                .vxEventId(vxEvent.id)
                .state(VxEventParticipant.State.active)
                .build();
        VxDsService.persist(vxEventParticipant, systemService.getVxBankDatastore(), VxEventParticipant.class);

        ModelMapper mm = new ModelMapper();
        EventJoinResponse response = mm.map(vxEvent, EventJoinResponse.class);

        List<VxEventParticipant> participantList =
                VxDsService.getParticipantsByEventId(systemService.getVxBankDatastore(),
                vxEvent.id);
        participantList = participantList.stream()
                .filter(p -> p.state == VxEventParticipant.State.active)
                .toList();
        response.eventActiveParticipantsCount = participantList.size();

        return response;
    }


    @GetMapping("/{eventId}")
    @ResponseBody
    public EventGetResponse get(@PathVariable Long eventId) {

        VxEvent vxEvent = VxDsService.getById(eventId, systemService.getVxBankDatastore(), VxEvent.class);
        if (vxEvent == null) {
            throw new IllegalStateException("Not able to locate event by id = " + eventId);
        }

        ModelMapper mm = new ModelMapper();
        EventGetResponse response = mm.map(vxEvent, EventGetResponse.class);

        List<VxEventPayment> payments = VxDsService.getVxEventPaymentList(systemService.getVxBankDatastore(), eventId);

        Long totalCredit = payments.stream()
                .filter(payment -> payment.type == VxEventPayment.Type.credit &&
                        payment.state == VxEventPayment.State.complete)
                .mapToLong(VxEventPayment::getValue)
                .sum();
        Long totalDebit = payments.stream()
                .filter(payment -> payment.type == VxEventPayment.Type.debit &&
                        payment.state == VxEventPayment.State.complete)
                .mapToLong(VxEventPayment::getValue)
                .sum();

        Long availableFunds = totalCredit - totalDebit;
        response.availableFunds = availableFunds;

        return response;

    }

    @GetMapping
    @ResponseBody
    public EventSearchResponse search(@RequestParam(name = "vxIntegrationId") VxIntegrationId vxIntegrationId,
                                      @RequestParam(name = "stateList") List<VxEvent.State> stateList,
                                      @RequestParam(name = "offset", defaultValue = "0") Long offset,
                                      @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        List<VxEvent> vxEventList = VxDsService.searchEvent(systemService.getVxBankDatastore(),
                vxIntegrationId.toString(),
                stateList);

        EventSearchResponse searchResponse = new EventSearchResponse();
        searchResponse.eventList = vxEventList;

        return searchResponse;

    }

    @PostMapping("/closeEvent")
    public EventCloseResponse closeEvent(Authentication auth, @RequestBody EventCloseParams params) throws
            StripeException {

        VxUser currentUser = systemService.validateUserAndStripeConfig(auth);

        VxEvent vxEvent = VxDsService.getById(VxEvent.class, systemService.getVxBankDatastore(), params.vxEventId);
        if (VxEvent.Type.payed1V1.equals(vxEvent.type)) {
            closePayed1v1Event(currentUser, vxEvent);
        }

        EventCloseResponse response = EventCloseResponse.newInstance(vxEvent);
        return response;

    }

    private boolean userIsParticipant(VxUser vxUser, List<VxEventParticipant> list) {
        Optional<VxEventParticipant> optionalParticipant = list.stream()
                .filter(p -> p.vxUserId.equals(vxUser.id) && p.state.equals(VxEventParticipant.State.active))
                .findFirst();
        return optionalParticipant.isPresent();
    }

    private boolean resultsAreGood1v1Results(List<VxEventResult> resultList, List<VxEventParticipant> participantList) {
        Set<Long> participantSet = participantList.stream()
                .map(p -> p.vxUserId)
                .collect(Collectors.toSet());

        // we need 2 participants
        if (participantSet.size() != 2) {
            throw new IllegalStateException("We need 2 participatns");
            //return false;
        }

        List<VxEventResult> activeResults = resultList.stream()
                .filter(r -> r.state.equals(VxEventResult.State.active))
                .collect(Collectors.toList());

        // all participants need to have a proposal
        Set<Long> whoDidNotUpdatedResults = new HashSet<>(participantSet);
        activeResults.forEach(r -> whoDidNotUpdatedResults.remove(r.vxEventId));
        if (whoDidNotUpdatedResults.size() > 0) {
            throw new IllegalStateException("All participants need to have a proposal");
            //return false;
        }

        // all participantFinalResultPlace need to be the same
        for (VxEventResult result : activeResults) {
            if (!result.participantFinalResultPlace.equals(VxEventResult.FinalResultPlace.firstPlace)) {
                throw new IllegalStateException("All participants to to propose the same result");
                //return false;
            }
        }

        // prize value needs to be positive and the same
        Set<Long> proposedValues = new HashSet<>();
        for (VxEventResult result : activeResults) {
            if (result.prizeValue < 0L) {
                throw new IllegalStateException("All values need to be positive");
                //return false;
            }
            proposedValues.add(result.prizeValue);
        }
        if (proposedValues.size() != 1) {
            throw new IllegalStateException("All values need to be the same");
            //return false;
        }

        return true;
    }

    private void closePayed1v1Event(VxUser currentUser, VxEvent vxEvent) {

        // check current user is participant
        List<VxEventParticipant> participantList = VxDsService.getListByEventId(VxEventParticipant.class,
                systemService.getVxBankDatastore(),
                vxEvent.id);
        if (!userIsParticipant(currentUser, participantList)) {
            throw new IllegalStateException("You are not a participant. You are not allowed to close this event");
        }

        List<VxEventResult> resultList = VxDsService.getListByEventId(VxEventResult.class,
                systemService.getVxBankDatastore(),
                vxEvent.id);
        if (!resultsAreGood1v1Results(resultList, participantList)) {
            throw new IllegalStateException("Not good 1v1 results");
        }

        throw new IllegalStateException("Please implement this: close1v1");
    }


}
