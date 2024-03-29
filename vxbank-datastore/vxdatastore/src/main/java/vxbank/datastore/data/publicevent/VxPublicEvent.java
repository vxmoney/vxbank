package vxbank.datastore.data.publicevent;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VxPublicEvent {

    @Id
    public Long id;
    @Index
    public Long vxUserId;
    public List<Long> managerIdList;
    @Index
    public String vxIntegrationId;

    public String title;

    public String currency;

    @Index
    public Long createTimeStamp;
}
