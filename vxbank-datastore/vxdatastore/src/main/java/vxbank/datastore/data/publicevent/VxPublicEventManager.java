package vxbank.datastore.data.publicevent;

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
public class VxPublicEventManager {
    @Id
    public Long id;
    @Index
    public Long userId;
    @Index
    public Long publicEventId;
}
