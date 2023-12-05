package vxbank.datastore.data.service;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxDataTestEntity;
import vxbank.datastore.data.models.VxUser;

import java.util.Optional;

public class VxdTestEntityService {

    public static VxDataTestEntity persist(VxDataTestEntity PDMyTestEntity, VxBankDatastore vd ) {
        vd.ofy.save()
                .entity(PDMyTestEntity)
                .now();
        return PDMyTestEntity;
    }


}
