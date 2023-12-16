package eu.vxbank.api.testutils;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxModel;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.service.VxDsService;

public class SetupUtils {
    public static VxUser createVxUser(VxUser vxUser, VxBankDatastore vxStore){

        VxDsService.persist(vxUser,vxStore);
        return vxUser;
    }

    public static VxModel persistVxModel(VxModel vxModel, VxBankDatastore ds) {
        VxDsService.persist(vxModel,ds);
        return vxModel;
    }
}
