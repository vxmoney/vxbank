package vxbank.datastore.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.commands.close1v1event.VxDsCommand;

import java.util.Optional;

public class CommandExampleTest {



    private VxBankDatastore ds = VxBankDatastore.init("my-project",
            VxBankDatastore.ConnectionType.localhost,
            Optional.empty());

    class CommandExample extends VxDsCommand{

        VxBankDatastore mds;
        public Long idUserPayment;
        public Long idVxGamingPayment;
        private CommandExample(VxBankDatastore mds){
            this.mds = mds;
        }

        @Override
        public void run() {
            idUserPayment = 1L;
            idVxGamingPayment = 2L;
        }
    }

    @Test
    void close1v1EventTest() {
        CommandExample example = new CommandExample(ds);
        example.run();
        Assertions.assertNotNull(example.idUserPayment);
        Assertions.assertNotNull(example.idVxGamingPayment);
    }
}
