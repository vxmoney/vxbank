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
public class VxPublicEventOrderItem {

    public enum IndexedField {
        vxPublicEventProductId,
        vxPublicEventId,
        vxPublicEventSellingPointId,
        vxPublicClientPaymentId,
        vxPublicEventClientId,
        vxPublicEventManagerUserId
    }

    @Id
    public Long id;
    @Index
    public Long vxPublicEventProductId;


    @Index
    public Long vxPublicEventId;
    @Index
    public Long vxPublicEventSellingPointId;

    @Index
    public Long vxPublicClientPaymentId;
    @Index
    public Long vxPublicEventClientId;
    @Index
    public Long vxPublicEventManagerUserId;
    @Index
    public Long timeStamp;

    public Long quantity;
    public Long value;
}
