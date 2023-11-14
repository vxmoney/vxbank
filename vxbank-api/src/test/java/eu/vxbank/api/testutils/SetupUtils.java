package eu.vxbank.api.testutils;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxModel;
import vxbank.datastore.data.models.VxServiceIntegration;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxUserService;

public class SetupUtils {
    public static VxUser createVxUser(VxUser vxUser, VxBankDatastore vxStore){

        VxUserService.persist(vxUser,vxStore);
        return vxUser;
    }

    public static VxModel persistVxModel(VxModel vxModel, VxBankDatastore ds) {
        VxUserService.persist(vxModel,ds);
        return vxModel;
    }
}
