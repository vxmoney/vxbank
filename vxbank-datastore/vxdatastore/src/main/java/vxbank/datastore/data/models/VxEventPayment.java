package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vxbank.datastore.data.publicevent.VxPublicEventClientPayment;

@Entity
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VxEventPayment {
    public enum Type{
        credit,
        debit
    }

    public enum State{
        pending,
        complete
    }

    @Id
    public Long id;

    // <publicEvent section>
    @Index
    public String vxIntegrationId; //values: vxGaming vxBank vxEvents
    @Index
    public Long vxPublicEventId;
    @Index
    public Long vxPublicEventClientId;
    @Index
    public VxPublicEventClientPayment.Method vxPublicEventClientPaymentMethod;
    // </publicEvent section>

    @Index
    public Long vxEventId;

    @Index
    public Long vxUserId;

    @Index
    public Type type;

    @Index
    public String stripeSessionId;
    public String stripeSessionPaymentUrl;

    // relevant when funds are managed directly by our platform
    public String stripeTransferId;

    public String currency;

    public Long value;
    public Long netValue;

    public State state;

    public String description;

}
