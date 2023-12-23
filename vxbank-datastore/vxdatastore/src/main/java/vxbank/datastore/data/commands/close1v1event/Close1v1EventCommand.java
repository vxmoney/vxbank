package vxbank.datastore.data.commands.close1v1event;

import vxbank.datastore.VxBankDatastore;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Close1v1EventCommand {

    public static final String VX_GAMING_PAYMENT_ID = "VX_GAMING_PAYMENT_ID_PLACEHOLDER";
    public static final String WINNER_PAYMENT_ID = "WINNER_PAYMENT_ID";


    private VxBankDatastore ds;
    private Long vxEventId;

    private Close1v1EventCommand(VxBankDatastore ds, Long vxEventId) {
        this.ds = ds;
        this.vxEventId = vxEventId;
    }

    public static Map<String, Long> execute(VxBankDatastore ds, Long vxEventId) {
        Map<String, Long> result = new HashMap<>();
        result.put(VX_GAMING_PAYMENT_ID, 1L);
        result.put(WINNER_PAYMENT_ID, 2L);
        return result;
    }
}
