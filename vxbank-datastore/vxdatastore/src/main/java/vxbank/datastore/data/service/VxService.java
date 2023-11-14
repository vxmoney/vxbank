package vxbank.datastore.data.service;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxModel;
import vxbank.datastore.data.models.VxUser;

public class VxService {
    public static VxUser persist(VxUser vxUser, VxBankDatastore vd){
        vd.ofy.save().entity(vxUser).now();
        return vxUser;
    }
    public static VxModel persist(VxModel vxModel, VxBankDatastore ds){
        ds.ofy.save().entity(vxModel).now();
        return vxModel;
    }

    public static <T> T get(Long modelId, Class<T> vxClass, VxBankDatastore ds){
        T vxModel = ds.ofy.load().type(vxClass).id(modelId).now();
        return vxModel;
    }
}
