package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class VxStripeConfig {


    public enum State {
        notConfigured, configurationInProgress, active, restricted
    }


    @Id
    public Long id;

    @Index
    public Long userId;

    @Index
    public String stripeAccountId;

    public State state;

    @Index
    public String currency;

}
