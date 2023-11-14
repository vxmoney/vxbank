package vxbank.datastore.data.service;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxModel;
import vxbank.datastore.data.models.VxServiceIntegration;
import vxbank.datastore.data.models.VxUser;

public class VxUserService {
    public static VxUser persist(VxUser vxUser, VxBankDatastore vd){
        vd.ofy.save().entity(vxUser).now();
        return vxUser;
    }
    public static VxModel persist(VxModel vxModel, VxBankDatastore ds){
        ds.ofy.save().entity(vxModel).now();
        return vxModel;
    }
}
