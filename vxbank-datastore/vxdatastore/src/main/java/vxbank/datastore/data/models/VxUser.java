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
public class VxUser {

    public enum StripeConfigState {
        configurationInitiated, active;
    }

    @Id
    public Long id;

    @Index
    public String email;

    public StripeConfigState active;

}
