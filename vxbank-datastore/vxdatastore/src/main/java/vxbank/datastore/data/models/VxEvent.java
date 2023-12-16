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
public class VxEvent implements VxModel {
    public enum Type{
        payed1V1
    }

    public enum State{
        openForRegistration,
        inProgress,
        closed,
    }

    @Id
    public Long id;

    @Index
    public Long vxUserId;
    public Long createTimeStamp;
    public String currency;
    public Long entryPrice;

}
