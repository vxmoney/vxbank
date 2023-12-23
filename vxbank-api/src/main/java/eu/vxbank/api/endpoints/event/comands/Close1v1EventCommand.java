package eu.vxbank.api.endpoints.event.comands;

import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import eu.vxbank.api.endpoints.event.dto.EventCloseParams;
import eu.vxbank.api.utils.components.vxintegration.VxIntegration;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationId;
import eu.vxbank.api.utils.stripe.VxStripeUtil;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.VxDsCommand;
import vxbank.datastore.data.models.*;
import vxbank.datastore.data.service.VxDsService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Close1v1EventCommand extends VxDsCommand {


    private Long currentUserId;
    private EventCloseParams params;

    VxIntegration vxGaming;
    String stripeSecretKey;
    private VxUser vxUser;
    private VxEvent vxEvent;
    private List<VxEventParticipant> participantList;
    private  List<VxEventResult> resultList;


    public Close1v1EventCommand(VxBankDatastore ds, Long currentUserId, EventCloseParams params,  VxIntegration vxGaming,
                                String stripeSecretKey) {
        super(ds);
        this.currentUserId = currentUserId;
        this.params = params;
        this.vxGaming = vxGaming;
        this.stripeSecretKey = stripeSecretKey;
    }

    @Override
    public void run() {

        checkCurrentUserIsParticipant();
        checkAllAreGoodResults();

        try {
            checkEventPaymentsMatch1v1ResultsThenProcessThem(params.vxEventId);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private Long computePercentage(Long value, Long percentage) {
        Long result = (value * percentage) / (100 * 100);
        return result;
    }

    private void checkEventPaymentsMatch1v1ResultsThenProcessThem(Long eventId) throws StripeException {
        List<VxEventResult> resultList = VxDsService.getListByEventId(VxEventResult.class,
                getDs(),
                eventId);
        Optional<VxEventResult> optionalVxEventResult = resultList.stream()
                .filter(r -> r.state.equals(VxEventResult.State.active))
                .findAny();
        if (optionalVxEventResult.isEmpty()) {
            throw new IllegalStateException("We have no results");
        }

        List<VxEventPayment> payments = VxDsService.getListByEventId(VxEventPayment.class,
                getDs(),
                eventId);
        Long totalDebit = payments.stream()
                .filter(p -> p.type.equals(VxEventPayment.Type.debit))
                .mapToLong(VxEventPayment::getValue)
                .sum();
        Long totalCredit = payments.stream()
                .filter(p -> p.type.equals(VxEventPayment.Type.credit))
                .mapToLong(VxEventPayment::getValue)
                .sum();
        Long availableFunds = totalDebit - totalCredit;
        if (availableFunds <= 0) {
            throw new IllegalStateException("There are no funds available availableFunds=" + availableFunds);
        }

        Long vxGamingFees = computePercentage(availableFunds, vxGaming.integrationPercentage);

        if (vxGamingFees <= 0) {
            throw new IllegalStateException("gaming fees are not positive");
        }
        if (vxGamingFees >= availableFunds) {
            throw new IllegalStateException("gaming fees are grater then available funds");
        }

        VxEvent vxEvent = VxDsService.getById(VxEvent.class, getDs(), eventId);
        if (vxEvent.state == VxEvent.State.closed) {
            throw new IllegalStateException("Event is already closed");
        }

        // build fees to vxGaming
        VxEventPayment vxFeesPayment = VxEventPayment.builder()
                .vxEventId(eventId)
                .vxUserId(vxGaming.vxUserId)
                .type(VxEventPayment.Type.credit)
                .currency(vxEvent.currency)
                .value(vxGamingFees)
                .state(VxEventPayment.State.complete)
                .description("VxGaming fees")
                .build();

        // build prise to winner;
        VxEventResult result = optionalVxEventResult.get();
        Long winnerUserId = result.participantId;
        List<VxStripeConfig> winnerConfigList = VxDsService.getByUserId(VxStripeConfig.class,
                getDs(),
                winnerUserId);
        if (winnerConfigList.size() != 1) {
            throw new IllegalStateException("winnerConfigList.size()=" + winnerConfigList.size());
        }
        String winnerStripeId = winnerConfigList.get(0).stripeAccountId;
        Long priseValue = availableFunds - vxGamingFees;

        VxEventPayment vxPrisePayment = VxEventPayment.builder()
                .vxEventId(eventId)
                .vxUserId(winnerUserId)
                .type(VxEventPayment.Type.credit)
                .currency(vxEvent.currency)
                .value(priseValue)
                .state(VxEventPayment.State.complete)
                .description("VxGaming 1v1 winner")
                .build();


        // send fees to vxGaming
        Transfer vxGamingTransfer = VxStripeUtil.sendFundsToStripeAccount(stripeSecretKey,
                vxGaming.vxStripeId,
                vxGamingFees,
                vxEvent.currency);
        Transfer vxWinnerTransfer = VxStripeUtil.sendFundsToStripeAccount(stripeSecretKey,
                winnerStripeId,
                priseValue,
                vxEvent.currency);

        // set transfer id values
        vxFeesPayment.setStripeTransferId(vxGamingTransfer.getId());
        vxPrisePayment.setStripeTransferId(vxWinnerTransfer.getId());

        // persist fees
        VxDsService.persist(VxPayment.class, getDs(), vxFeesPayment);
        VxDsService.persist(VxPayment.class, getDs(), vxPrisePayment);

        // close the event
        vxEvent.state = VxEvent.State.closed;
        VxDsService.persist(VxEvent.class, getDs(), vxEvent);
    }

    private void checkAllAreGoodResults() {
        VxDsService.transactionLess(getDs(), () ->{
            resultList = VxDsService.getListByEventId(VxEventResult.class,
                    getDs(),
                    vxEvent.id);

            if (!resultsAreGood1v1Results(resultList,participantList)){
                throw new IllegalStateException("Not good 1v1 results");
            }

        });
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
        activeResults.forEach(r -> whoDidNotUpdatedResults.remove(r.vxUserId));
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

    private void checkCurrentUserIsParticipant() {

        VxDsService.transactionLess(getDs(), () ->{

            vxUser = VxDsService.getById(VxUser.class, getDs(), currentUserId);
            vxEvent = VxDsService.getById(VxEvent.class, getDs(),params.vxEventId);

            participantList = VxDsService
                    .getListByEventId(VxEventParticipant.class, getDs(), params.vxEventId);

            if (!userIsParticipant(currentUserId,participantList)){
                throw new IllegalStateException("You are not a participant. You are not allowed to close this event");
            }
        });
    }

    private boolean userIsParticipant(Long vxUserId , List<VxEventParticipant> list) {
        Optional<VxEventParticipant> optionalParticipant = list.stream()
                .filter(p -> p.vxUserId.equals(vxUserId) && p.state.equals(VxEventParticipant.State.active))
                .findFirst();
        return optionalParticipant.isPresent();
    }
}
