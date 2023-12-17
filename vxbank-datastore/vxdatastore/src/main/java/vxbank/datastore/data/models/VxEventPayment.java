package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Index
    public Long vxEventId;

    @Index
    public Long vxUserId;

    @Index
    public Type type;

    public String currency;

    public Long value;

    public State state;

    public String description;

}
