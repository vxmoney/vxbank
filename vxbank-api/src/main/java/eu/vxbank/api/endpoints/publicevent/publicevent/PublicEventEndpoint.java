package eu.vxbank.api.endpoints.publicevent.publicevent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import eu.vxbank.api.endpoints.payment.dto.StripeSessionCreateResponse;
import eu.vxbank.api.endpoints.publicevent.publicevent.dto.*;
import eu.vxbank.api.endpoints.publicevent.tools.PublicEventEndpointTools;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegration;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.enums.Environment;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vxbank.datastore.data.models.VxEventPayment;
import vxbank.datastore.data.models.VxStripeConfig;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.publicevent.VxPublicEvent;
import vxbank.datastore.data.publicevent.VxPublicEventClient;
import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;
import vxbank.datastore.data.publicevent.VxPublicEventManager;
import vxbank.datastore.data.service.VxDsService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publicEvent")
public class PublicEventEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @PostMapping
    public PublicEventCreateResponse create(Authentication auth, @RequestBody PublicEventCreateParams params) throws
            StripeException {

        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);

        if (!Objects.equals(vxUser.id, params.vxUserId)) {
            throw new IllegalStateException("You can not create events for someone else");
        }

        Long timeStamp = System.currentTimeMillis();

        VxPublicEvent publicEvent = VxPublicEvent.builder()
                .vxUserId(vxUser.id)
                .vxIntegrationId(params.vxIntegrationId.toString())
                .title(params.title)
                .currency(params.currency)
                .createTimeStamp(timeStamp)
                .build();
        VxDsService.persist(VxPublicEvent.class, systemService.getVxBankDatastore(), publicEvent);

        VxPublicEventManager publicEventManager = VxPublicEventManager.builder()
                .userId(vxUser.id)
                .publicEventId(publicEvent.id)
                .timeStamp(timeStamp)
                .build();
        VxDsService.persist(VxPublicEventManager.class, systemService.getVxBankDatastore(), publicEventManager);

        ModelMapper mm = new ModelMapper();
        PublicEventCreateResponse response = mm.map(publicEvent, PublicEventCreateResponse.class);
        response.managerIdList = Collections.singletonList(vxUser.id);
        return response;
    }

    @GetMapping("/{eventId}")
    @ResponseBody
    public PublicEventCreateResponse get(Authentication auth, @PathVariable Long eventId) {
        VxUser vxUser = systemService.validateUserAndStripeConfig(auth);


        if (!userIsPublicEventManger(vxUser.id, eventId)) {
            throw new IllegalStateException("You are not VxPublicEvent manager");
        }

        // get event
        VxPublicEvent publicEvent = VxDsService.getById(VxPublicEvent.class,
                systemService.getVxBankDatastore(),
                eventId);
        ModelMapper mm = new ModelMapper();
        PublicEventCreateResponse response = mm.map(publicEvent, PublicEventCreateResponse.class);

        // get managers
        List<VxPublicEventManager> managerList = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                eventId);
        List<Long> managerIdList = managerList.stream()
                .map(VxPublicEventManager::getUserId)
                .toList();
        response.managerIdList = managerIdList;

        return response;
    }

    private boolean userIsPublicEventManger(Long id, Long eventId) {
        List<VxPublicEventManager> eventList = VxDsService.getByUserId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                id);
        Optional<VxPublicEventManager> optionalVxPublicEventManager = eventList.stream()
                .filter(pe -> pe.publicEventId.equals(eventId))
                .findFirst();
        return optionalVxPublicEventManager.isPresent();
    }

    @GetMapping
    @ResponseBody
    public PublicEventSearchResponse search(Authentication auth, @RequestParam(name = "vxUserId") Long vxUserId) {

        VxUser vxUser = systemService.validateAndGetUser(auth);

        if (!Objects.equals(vxUser.id, vxUserId)) {
            throw new IllegalStateException("You can not search events for someone else");
        }

        List<VxPublicEventManager> managerList = VxDsService.getByUserId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                vxUserId);
        //order list descending by timeStamp
        managerList.sort(Comparator.comparing(VxPublicEventManager::getTimeStamp).reversed());

        List<Long> publicEventIdSet = managerList.stream()
                .map(VxPublicEventManager::getPublicEventId).toList();


        List<VxPublicEvent> vxPublicEventList = VxDsService.getByIdList(systemService.getVxBankDatastore(),
                VxPublicEvent.class, publicEventIdSet);

        PublicEventSearchResponse response = new PublicEventSearchResponse();
        response.eventList = vxPublicEventList;
        return response;
    }

    @PostMapping("/{eventId}/managers")
    public PublicEventAddManagerResponse managersAddManager(Authentication auth,
                                                            @PathVariable Long eventId,
                                                            @RequestBody PublicEventAddMangerParams params) throws
            StripeException {

        // check stuff
        VxUser currentUser = systemService.validateAndGetUser(auth);
        VxPublicEvent vxPublicEvent = getVxEvent(params.publicEventId);
        checkUserIsOwnerOfEvent(currentUser, vxPublicEvent);
        VxUser vxUser = checkGetUserByEmail(params.email);
        checkUserIsNotMangerForEvent(vxUser, vxPublicEvent);

        // add manager
        VxPublicEventManager publicEventManager = VxPublicEventManager.builder()
                .userId(vxUser.id)
                .publicEventId(vxPublicEvent.id)
                .build();
        VxDsService.persist(VxPublicEventManager.class, systemService.getVxBankDatastore(), publicEventManager);

        PublicEventAddManagerResponse response = PublicEventAddManagerResponse.builder()
                .id(publicEventManager.id)
                .userId(vxUser.id)
                .email(vxUser.email)
                .publicEventId(vxPublicEvent.id)
                .timeStamp(publicEventManager.timeStamp)
                .build();

        return response;
    }

    private void checkUserIsNotMangerForEvent(VxUser vxUser, VxPublicEvent vxPublicEvent) {
        List<VxPublicEventManager> managerList = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                vxPublicEvent.id);
        Set<Long> managersSet = managerList.stream().map(m -> m.userId).collect(Collectors.toSet());
        if (managersSet.contains(vxUser.id)) {
            throw new IllegalStateException("User is already a manager for this event");
        }
    }

    private VxUser checkGetUserByEmail(String email) {
        Optional<VxUser> user = VxDsService.getUserByEmail(email, systemService.getVxBankDatastore());
        if (user.isEmpty()) {
            throw new IllegalStateException("No user by email " + email);
        }
        return user.get();
    }

    private VxPublicEvent getVxEvent(Long publicEventId) {
        VxPublicEvent vxPublicEvent = VxDsService.getById(VxPublicEvent.class, systemService.getVxBankDatastore(), publicEventId);
        return vxPublicEvent;
    }

    @DeleteMapping("/{eventId}/managers/{email}")
    public String managersDeleteManager(Authentication auth,
                                        @PathVariable Long eventId,
                                        @PathVariable String email) throws
            StripeException {

        VxUser currentUser = systemService.validateAndGetUser(auth);
        VxPublicEvent vxPublicEvent = getVxEvent(eventId);
        checkUserIsOwnerOfEvent(currentUser, vxPublicEvent);
        VxUser vxUser = checkGetUserByEmail(email);
        PublicEventEndpointTools. checkUserIsManagerOfEvent(systemService.getVxBankDatastore(), vxUser, eventId);

        List<VxPublicEventManager> managerList = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(),
                eventId);
        VxPublicEventManager vxPublicEventManager = managerList.stream()
                .filter(m -> m.userId.equals(vxUser.id))
                .findFirst().get();
        VxDsService.delete(systemService.getVxBankDatastore(), vxPublicEventManager);
        return "OK";
    }

    @GetMapping("/{eventId}/managers")
    @ResponseBody
    public PublicEventGetManagerListResponse managersGetManagers(Authentication auth, @PathVariable Long eventId) {

        VxUser vxUser = systemService.validateAndGetUser(auth);

        List<VxPublicEventManager> managerList = VxDsService.getByPublicEventId(VxPublicEventManager.class,
                systemService.getVxBankDatastore(), eventId);

        Set<Long> managersSet = managerList.stream().map(m -> m.userId).collect(Collectors.toSet());
        if (!managersSet.contains(vxUser.id)) {
            throw new IllegalStateException("You are not a manger for this event");
        }

        List<Long> mangersIdList = managerList.stream().map(m -> m.userId).toList();
        List<VxUser> managersList = VxDsService.getByIdList(systemService.getVxBankDatastore(), VxUser.class, mangersIdList);
        PublicEventGetManagerListResponse response = new PublicEventGetManagerListResponse();
        response.managerList = managersList;

        return response;
    }

    public void checkUserIsOwnerOfEvent(VxUser vxUser, VxPublicEvent vxPublicEvent) {
        if (vxPublicEvent.vxUserId != vxUser.id) {
            throw new IllegalStateException("User is not Owner of event");
        }
    }



    @GetMapping("/{eventId}/checkRegisterClient")
    @ResponseBody
    public PublicEventCheckRegisterClientResponse checkRegisterClient(Authentication auth, @PathVariable Long eventId) {

        VxUser vxUser = systemService.validateAndGetUser(auth);
        checkPublicEventExists(eventId);
        List<VxPublicEventClient> clientList = VxDsService.getByPublicEventId(VxPublicEventClient.class,
                systemService.getVxBankDatastore(),
                eventId);
        // check if user is already in clientList
        Optional<VxPublicEventClient> optionalVxPublicEventClient = clientList.stream()
                .filter(c -> c.userId.equals(vxUser.id))
                .findFirst();
        if (optionalVxPublicEventClient.isEmpty()) {
            // user is not in clientList so we add him
            Long timeStamp = System.currentTimeMillis();
            VxPublicEventClient newEventClient = VxPublicEventClient.builder()
                    .userId(vxUser.id)
                    .publicEventId(eventId)
                    .timeStamp(timeStamp)
                    .build();
            VxDsService.persist(VxPublicEventClient.class, systemService.getVxBankDatastore(), newEventClient);
            optionalVxPublicEventClient = Optional.of(newEventClient);
        }
        VxPublicEventClient client = optionalVxPublicEventClient.get();

        //map to response using objectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        PublicEventCheckRegisterClientResponse response = objectMapper.convertValue(client, PublicEventCheckRegisterClientResponse.class);
        return response;
    }

    private void checkPublicEventExists(Long eventId) {
        VxPublicEvent vxPublicEvent = VxDsService.getById(VxPublicEvent.class, systemService.getVxBankDatastore(), eventId);
        if (vxPublicEvent == null) {
            throw new IllegalStateException("Public event does not exist");
        }
    }

    private VxPublicEventClient checkGetUserIsClientForEvent(VxUser vxUser, VxPublicEvent vxPublicEvent) {
        List<VxPublicEventClient> clientList = VxDsService.getByPublicEventId(VxPublicEventClient.class,
                systemService.getVxBankDatastore(),
                vxPublicEvent.id);
        Optional<VxPublicEventClient> optionalVxPublicEventClient = clientList.stream()
                .filter(c -> c.userId.equals(vxUser.id))
                .findFirst();
        if (optionalVxPublicEventClient.isEmpty()) {
            throw new IllegalStateException("User is not a client for this event");
        }
        return optionalVxPublicEventClient.get();
    }

    private VxStripeConfig checkGetStripeConfigForEventOwner(Long vxUserId, String currency) throws StripeException {
        List<VxStripeConfig> configList = VxDsService.getByUserId(vxUserId,
                new HashMap<>(),
                systemService.getVxBankDatastore(),
                VxStripeConfig.class);
        Optional<VxStripeConfig> optionalConfig = configList.stream().findFirst();
        if (optionalConfig.isEmpty()) {
            throw new IllegalStateException("User needs to configure first bank for currency=" + currency);
        }
        VxStripeConfig stripeConfig = optionalConfig.get();
        Boolean canProcessCurrency = VxStripeUtil.clientCanReceivePaymentInCurrency(stripeKeys.stripeSecretKey,
                stripeConfig.stripeAccountId,
                currency);
        if (!canProcessCurrency) {
            throw new IllegalStateException("Event owner can not process currency=" + currency);
        }
        return stripeConfig;
    }

    // implement clientDepositFunds endpoint
    @PostMapping("/{eventId}/clientDepositFunds")
    public PublicEventClientDepositFundsResponse clientDepositFunds(Authentication auth,
                                                                    @PathVariable Long eventId,
                                                                    @RequestBody PublicEventClientDepositFundsParams params) throws
            StripeException {

        VxUser vxUser = systemService.validateAndGetUser(auth);
        checkPublicEventExists(eventId);
        VxPublicEvent vxPublicEvent = getVxEvent(eventId);
        VxPublicEventClient vxPublicEventClient = checkGetUserIsClientForEvent(vxUser, vxPublicEvent);

        VxStripeConfig vxStripeConfig = checkGetStripeConfigForEventOwner(vxPublicEvent.vxUserId, vxPublicEvent.currency);


        Long vxPublicEventId = vxPublicEvent.id;
        String vxPublicEventTitle = vxPublicEvent.title;
        Long vxPublicEventClientId = vxPublicEventClient.id;
        String currency = vxPublicEvent.currency;
        StripeSessionCreateResponse stripeSessionCreateResponse = createStripeSessionClientDepositFunds(stripeKeys.stripeSecretKey,
                vxStripeConfig,
                vxPublicEventId,
                vxPublicEventTitle,
                currency,
                params.value);

        // create vxEventPayment
        Long timeStamp = System.currentTimeMillis();
        VxEventPayment vxEventPayment = VxEventPayment.builder()
                .vxIntegrationId(VxIntegrationId.vxEvents.toString())
                .vxPublicEventClientPaymentMethod(VxPublicEventClientPayment.Method.clientDepositFiat)
                .vxPublicEventId(vxPublicEventId)
                .vxPublicEventClientId(vxPublicEventClientId)
                .stripeSessionId(stripeSessionCreateResponse.stripeSessionId) // very important. Stripe session id
                .stripeSessionPaymentUrl(stripeSessionCreateResponse.url)
                .type(VxEventPayment.Type.debit)
                .state(VxEventPayment.State.pending)
                .currency(currency)
                .value(params.value)
                .build();
        VxDsService.persist(VxEventPayment.class, systemService.getVxBankDatastore(), vxEventPayment);

        PublicEventClientDepositFundsResponse response = new PublicEventClientDepositFundsResponse();
        response.vxPublicEventClientId = vxPublicEventClientId;
        response.vxPublicEventId = vxPublicEventId;
        response.vxEventPaymentId = vxEventPayment.id;
        response.stripeSessionPaymentUrl = stripeSessionCreateResponse.url;
        response.stripeSessionId = stripeSessionCreateResponse.stripeSessionId;
        return response;
    }

    private StripeSessionCreateResponse createStripeSessionClientDepositFunds(String stripeSecretKey,
                                                                              VxStripeConfig vxStripeConfig,
                                                                              Long vxPublicEventId,
                                                                              String vxPublicEventTitle,
                                                                              String currency,
                                                                              Long value) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        double messageValue = (Double.valueOf(value) / 100.0); // 2 decimals
        String message = String.format("Deposit %.2f %s for %s", messageValue, currency, vxPublicEventTitle);

        PriceCreateParams priceParams = PriceCreateParams.builder()
                .setCurrency(currency)
                .setProductData(
                        PriceCreateParams.ProductData.builder()
                                .setName(message)
                                .build()
                )
                .setUnitAmount(value)
                .build();
        Price price = Price.create(priceParams);

        // Line item details
        SessionCreateParams.LineItem.Builder lineItemBuilder = SessionCreateParams.LineItem.builder()
                .setPrice(price.getId()) // Use the ID of the dynamically created price
                .setQuantity(1L);

        String successUrl = getStripeRefreshRedirectUrlForVxEvents(vxPublicEventId, systemService.getEnvironment());
        String cancelUrl = getStripeCancelUrlForVxEvents(vxPublicEventId, systemService.getEnvironment());

        // Session parameters
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(lineItemBuilder.build())
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl);

        SessionCreateParams.PaymentIntentData.Builder paymentIntentDataBuilder = SessionCreateParams.PaymentIntentData.builder();


        VxIntegration eventsIntegration = vxIntegrationConfig.getIntegrationById(VxIntegrationId.vxEvents);
        Long percentage = eventsIntegration.getIntegrationPercentage();
        Long applicationFee = value * percentage / 100;
        paymentIntentDataBuilder.setApplicationFeeAmount(applicationFee)
                .setTransferData(
                        SessionCreateParams.PaymentIntentData.TransferData.builder()
                                .setDestination(vxStripeConfig.stripeAccountId)
                                .build()
                );

        paramsBuilder.setPaymentIntentData(paymentIntentDataBuilder.build());

        SessionCreateParams params = paramsBuilder.build();

        Session session = Session.create(params);

        StripeSessionCreateResponse stripeSessionResponse = new StripeSessionCreateResponse();
        stripeSessionResponse.url = session.getUrl();
        stripeSessionResponse.stripeSessionId = session.getId();
        return stripeSessionResponse;
    }

    private String getStripeRefreshRedirectUrlForVxEvents(Long vxEventId, Environment environment) {
        ///home/bogdan/workspace/vxbank/vxevents-web-app-v1/src/app/appClient/publicEvent/[eventId]/hello/page.js
        String endUrl = String.format("appClient/publicEvent/%s", vxEventId);
        switch (environment) {
            case LOCALHOST -> {
                return "http://localhost:3000/" + endUrl;
            }
            case DEVELOPMENT -> {
                return "https://vxevents-dot-vxbank-eu-dev.ew.r.appspot.com/" + endUrl;
            }
            case PRODUCTION -> {
                return "https://vxevents-dot-vxbank-eu-prod.ew.r.appspot.com/" + endUrl;
            }
            default -> throw new IllegalStateException(
                    "getStripeRefreshRedirectUrl Not yet available in env " + environment);

        }
    }

    private String getStripeCancelUrlForVxEvents(Long vxEventId, Environment environment) {
        ///home/bogdan/workspace/vxbank/vxevents-web-app-v1/src/app/appClient/publicEvent/[eventId]/hello/page.js
        String endUrl = String.format("appClient/publicEvent/%s/cancel", vxEventId);
        switch (environment) {
            case LOCALHOST -> {
                return "http://localhost:3000/" + endUrl;
            }
            case DEVELOPMENT -> {
                return "https://vxevents-dot-vxbank-eu-dev.ew.r.appspot.com/" + endUrl;
            }
            case PRODUCTION -> {
                return "https://vxevents-dot-vxbank-eu-prod.ew.r.appspot.com/" + endUrl;
            }
            default -> throw new IllegalStateException(
                    "getStripeRefreshRedirectUrl Not yet available in env " + environment);

        }
    }

}
