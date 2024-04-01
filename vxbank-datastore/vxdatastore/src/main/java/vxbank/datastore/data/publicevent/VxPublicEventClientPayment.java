package vxbank.datastore.data.publicevent;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vxbank.datastore.data.models.VxEventPayment;

@Entity
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VxPublicEventClientPayment {

    public enum State {
        pending,
        complete
    }

    public enum Type {
        credit,
        debit
    }

    public enum Method{
        clientDepositFiat
    }

    @Id
    public Long id;

    @Index
    public String vxIntegrationId; //values: vxGaming vxBank vxEvents
    @Index
    public Long vxPublicEventId;
    @Index
    public Long vxPublicEventClientId;
    @Index
    public Long vxEventPaymentId;
    @Index
    public String stripeSessionId;
    @Index
    public Type type;
    @Index
    public State state;
    @Index
    public Method method;
    public Long value;
    public String currency;
    @Index
    public Long timeStamp;
    @Index
    public Long updatedTimeStamp;
}
