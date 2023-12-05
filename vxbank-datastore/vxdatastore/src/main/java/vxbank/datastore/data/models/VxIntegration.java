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
public class VxIntegration implements VxModel {


    public enum Type {
        vxgaming, chessout
    }

    @Id
    public Long id;

    /**
     * This is the vxUserId that is responsible for this service.
     */
    @Index
    public Long userId;
    @Index
    public Type type;

    public String title;
    public String description;
}
