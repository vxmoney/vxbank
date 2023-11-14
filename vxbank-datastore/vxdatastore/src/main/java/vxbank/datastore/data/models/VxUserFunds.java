package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class VxUserFunds {
    @Id
    public Long id;
    @Index
    public Long vxServiceIntegrationId;
    @Index
    public Long serviceIntegrationUserId;
    public Long availableFunds;
}
