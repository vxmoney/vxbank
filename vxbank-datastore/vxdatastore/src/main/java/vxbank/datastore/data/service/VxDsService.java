package vxbank.datastore.data.service;

import com.googlecode.objectify.cmd.Query;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.*;
import vxbank.datastore.data.publicevent.VxPublicEvent;

import javax.swing.plaf.PanelUI;
import java.util.*;

public class VxDsService {
    public static VxUser persist(VxUser vxUser, VxBankDatastore vd) {
        vd.ofy.save()
                .entity(vxUser)
                .now();
        return vxUser;
    }

    public static VxModel persist(VxModel vxModel, VxBankDatastore ds) {
        ds.ofy.save()
                .entity(vxModel)
                .now();
        return vxModel;
    }

    public static <T> T get(Long modelId, Class<T> vxClass, VxBankDatastore ds) {
        T vxModel = ds.ofy.load()
                .type(vxClass)
                .id(modelId)
                .now();
        return vxModel;
    }

    public static Optional<VxUser> getUserByEmail(String email, VxBankDatastore ds) {

        VxUser vxUser = ds.ofy.load()
                .type(VxUser.class)
                .filter("email", email)
                .first()
                .now();
        return Optional.ofNullable(vxUser);

    }

    /**
     * This is deprecated. Use the one that starts with ds and class
     */
    @Deprecated
    public static <T> T getById(Long id, VxBankDatastore ds, Class<T> vxClass) {
        T vxModel = ds.ofy.load()
                .type(vxClass)
                .id(id)
                .now();
        if (vxModel == null) {
            throw new IllegalStateException("Illegal user id");
        }
        return vxModel;
    }

    public static <T> T getById(Class<T> vxClass, VxBankDatastore ds, Long id) {
        T vxModel = ds.ofy.load()
                .type(vxClass)
                .id(id)
                .now();
        if (vxModel == null) {
            throw new IllegalStateException("Illegal user id");
        }
        return vxModel;
    }

    @Deprecated
    public static <T> T persist(Object vxModel, VxBankDatastore ds, Class<T> vxClass) {
        T myObject = (T) vxModel;
        ds.ofy.save()
                .entity(myObject)
                .now();
        return myObject;
    }

    public static <T> T persist(Class<T> vxClass, VxBankDatastore ds, Object vxModel) {
        T myObject = (T) vxModel;
        ds.ofy.save()
                .entity(myObject)
                .now();
        return myObject;
    }


    @Deprecated
    public static <T> List<T> getByUserId(Long userId,
                                          Map<String, Object> filterList,
                                          VxBankDatastore ds,
                                          Class<T> vxClass) {

        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("userId", userId);

        for (Map.Entry<String, Object> entry : filterList.entrySet()) {
            query = query.filter(entry.getKey(), entry.getValue());
        }

        query = query.chunkAll();
        List<T> list = query.list();


        return list;
    }

    public static <T> List<T> getByUserId(Class<T> vxClass, VxBankDatastore ds, Long userId) {

        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("userId", userId);

        query = query.chunkAll();
        List<T> list = query.list();

        return list;
    }

    public static <T> List<T> getByPublicEventId(Class<T> vxClass, VxBankDatastore ds, Long publicEventId) {

        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("publicEventId", publicEventId);

        query = query.chunkAll();
        List<T> list = query.list();

        return list;
    }

    public static <T> List<T> getByVxPublicEventId(Class<T> vxClass, VxBankDatastore ds, Long vxPublicEventId) {

        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("vxPublicEventId", vxPublicEventId);

        query = query.chunkAll();
        List<T> list = query.list();

        return list;
    }

    public static List<VxEventPayment> getVxEventPaymentList(VxBankDatastore ds, Long vxEventId) {
        Query<VxEventPayment> query = ds.ofy.load()
                .type(VxEventPayment.class)
                .filter("vxEventId", vxEventId);
        List<VxEventPayment> list = query.chunkAll()
                .list();
        return list;
    }

    @Deprecated
    public static <T> List<T> getListByEventId(VxBankDatastore ds, Long vxEventId, Class<T> vxClass) {
        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("vxEventId", vxEventId);

        query = query.chunkAll();
        List<T> list = query.list();


        return list;
    }

    public static <T> List<T> getListByEventId(Class<T> vxClass, VxBankDatastore ds, Long vxEventId) {
        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("vxEventId", vxEventId);

        query = query.chunkAll();
        List<T> list = query.list();


        return list;
    }


    public static List<VxEvent> searchEvent(VxBankDatastore ds, String vxIntegrationId,
                                            VxGame vxGame,
                                            List<VxEvent.State> stateList) {
        Query<VxEvent> query = ds.ofy.load()
                .type(VxEvent.class);

        query = query.filter("vxIntegrationId", vxIntegrationId);
        query = query.filter("vxGame", vxGame);

        VxEvent.State[] eventValues = VxEvent.State.values();
        Set<VxEvent.State> eventStates = new HashSet<>(Arrays.asList(eventValues));
        for (VxEvent.State state : stateList) {
            eventStates.remove(state);
        }
        for (VxEvent.State state : eventStates) {
            query.filter("state !=", state);
        }

        // order by createTimeStamp
        query = query.order("-createTimeStamp");

        query = query.chunkAll();
        List<VxEvent> eventList = query.list();
        return eventList;
    }

    public static List<VxPublicEvent> searchPublicEvent(VxBankDatastore ds, Long vxUserId) {
        Query<VxPublicEvent> query = ds.ofy.load().type(VxPublicEvent.class);
        query = query.filter("vxUserId", vxUserId);
        // order by createTimeStamp
        query = query.order("-createTimeStamp");
        query = query.chunkAll();
        List<VxPublicEvent> eventList = query.list();
        return eventList;
    }


    public static List<VxEventParticipant> getParticipantsByEventId(VxBankDatastore ds, Long vxEventId) {
        Query<VxEventParticipant> query = ds.ofy.load()
                .type(VxEventParticipant.class);
        query = query.filter("vxEventId", vxEventId);
        List<VxEventParticipant> participantList = query.chunkAll()
                .list();
        return participantList;
    }

    public static void transactionLess(VxBankDatastore ds, Runnable runnable) {
        ds.ofy.transactionless(() -> {
            runnable.run();
        });
    }

    public static <T> List<T> getListByUserIdAndCurrencyEventId(Class<T> vxClass, VxBankDatastore ds,
                                                                Long userId,
                                                                String currency) {
        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("userId", userId)
                .filter("currency", currency);


        query = query.chunkAll();
        List<T> list = query.list();


        return list;
    }

    public static <T> List<T> getByIdList(VxBankDatastore ds,
                                          Class<T> vxClass,
                                          List<Long> idList) {
        Map<Long, T> result = ds.ofy.load()
                .type(vxClass)
                .ids(idList);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result.values().stream().toList();
    }

    public static <T> List<T> getByStripeSessionId(Class<T> vxClass, VxBankDatastore ds, String stripeSessionId) {
        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("stripeSessionId", stripeSessionId);

        query = query.chunkAll();
        List<T> list = query.list();


        return list;
    }

    public static <T> void delete(VxBankDatastore ds, T vxModel) {
        ds.ofy.delete()
                .entity(vxModel)
                .now();
    }

    public static <T> List<T> vxPublicEventClientId(Class<T> vxClass, VxBankDatastore ds, Long vxPublicEventClientId) {
        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter("vxPublicEventClientId", vxPublicEventClientId);

        query = query.chunkAll();
        List<T> list = query.list();


        return list;
    }

    public static <T> List<T> getByLongIndexField(Class<T> vxClass,
                                                  VxBankDatastore ds,
                                                  String indexedField,
                                                  Long fieldValue) {
        Query<T> query = ds.ofy.load()
                .type(vxClass)
                .filter(indexedField, fieldValue);
        query = query.order("-timeStamp");
        query = query.chunkAll();
        List<T> list = query.list();
        return list;
    }


}
