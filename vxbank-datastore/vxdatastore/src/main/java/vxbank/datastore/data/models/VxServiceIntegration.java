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
public class VxServiceIntegration implements VxModel{

    @Id
    public Long id;
    @Index
    public Long userId;
    public String title;
    public String description;
    public String vxbankSecret;
}
