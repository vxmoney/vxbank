package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class VxDataTestEntity {
    @Id
    public String objectId;
    public String message;
}
