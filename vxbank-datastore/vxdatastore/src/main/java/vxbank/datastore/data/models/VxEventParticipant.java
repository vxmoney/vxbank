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
public class VxEventParticipant {
    public enum State {
        active, archived
    }

    @Id
    public Long id;

    @Index
    public Long vxUserId;
    @Index
    public Long vxEventId;

    @Index
    public State state;

}
