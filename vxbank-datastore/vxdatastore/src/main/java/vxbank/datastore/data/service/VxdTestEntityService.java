package vxbank.datastore.data.service;

import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxDataTestEntity;

public class VxdTestEntityService {
    public static VxDataTestEntity persist(VxDataTestEntity PDMyTestEntity, VxBankDatastore vd ) {
        vd.ofy.save()
                .entity(PDMyTestEntity)
                .now();
        return PDMyTestEntity;
    }
}
