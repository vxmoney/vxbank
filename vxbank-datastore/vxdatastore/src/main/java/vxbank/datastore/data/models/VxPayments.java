package vxbank.datastore.data.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class VxPayments {

    public enum State {
        pending, processed
    }

    @Id
    public Long id;

    @Index
    public Long vxUserId;

    /**
     * vxServiceIntegrationId = company id
     */
    @Index
    public Long vxServiceIntegrationId;

    /**
     * company userId
     */
    @Index
    public Long serviceIntegrationUserId;

    @Index
    public State state;
    public Long createTimeStamp;
    public Long processedTimeStamp;


    public String currency;
    public String productName;
    public Long valuePayedByUser;

    public Long valueAvailableToUser;
    public String valueCollectedByStripe;
    public String valueCollectedByVxbank;

    public String stripeSessionId;

}
