package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class VxService {

    @Id
    public String id;
    public String title;
    public String description;
}
