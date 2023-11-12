package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class VxServiceIntegration {

    @Id
    public Long id;
    public String title;
    public String description;
    public String vxbankSecret;
}
