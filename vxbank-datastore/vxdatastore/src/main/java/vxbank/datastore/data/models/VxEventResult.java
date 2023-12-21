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
public class VxEventResult {
    public enum State{
        active,
        archived
    }
    public enum FinalResultPlace{
        firstPlace,
        secondPlace,
        thirdPlace,
    }

    @Id
    public Long id;

    @Index
    public Long vxUserId;
    @Index
    public Long vxEventId;
    public Long createTimeStamp;
    public Long updateTimeStamp;
    public Long participantId;
    public FinalResultPlace participantFinalResultPlace;
    public Long prizeValue;

}
