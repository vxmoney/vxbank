package vxbank.datastore.data.service;

import com.googlecode.objectify.cmd.Query;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEventPayment;
import vxbank.datastore.data.models.VxModel;
import vxbank.datastore.data.models.VxUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public static <T> T persist(Object vxModel, VxBankDatastore ds, Class<T> vxClass) {
        T myObject = (T) vxModel;
        ds.ofy.save()
                .entity(myObject)
                .now();
        return myObject;
    }

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

    public static List<VxEventPayment> getVxEventPaymentList(VxBankDatastore ds, Long vxEventId){
        Query<VxEventPayment> query = ds.ofy.load()
                .type(VxEventPayment.class)
                .filter("vxEventId", vxEventId);
        List<VxEventPayment> list = query.chunkAll().list();
        return list;
    }
}
