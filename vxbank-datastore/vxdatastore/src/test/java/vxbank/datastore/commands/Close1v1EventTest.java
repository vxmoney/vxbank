package vxbank.datastore.commands;

import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxEvent;
import vxbank.datastore.data.models.VxUser;
import vxbank.datastore.data.utils.TestingUtils;

import java.util.Optional;

public class Close1v1EventTest {



    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());

    @Test
    void close1v1EventTest() {
        VxUser vxUser = TestingUtils.generatePersistRandomUser(ds);
        Long price = 1000L;
        VxEvent vxEvent = TestingUtils.generatePersistEvent(ds, vxUser.id, VxEvent.Type.payed1V1, "eur", price);



       /* Map<String,Long> closeResult = Close1v1EventCommand.execute(ds,vxEvent.id, vxUser.id);
        Long vxGamingPaymentId = closeResult.get(Close1v1EventCommand.VX_GAMING_PAYMENT_ID);
        Long vxWinnerPaymentId = closeResult.get(Close1v1EventCommand.VX_GAMING_PAYMENT_ID);
        Assertions.assertNotNull(vxGamingPaymentId);
        Assertions.assertNotNull(vxWinnerPaymentId);*/
    }
}
