package eu.vxbank.api.endpoints.publicevent.publicevent.dto;

import vxbank.datastore.data.publicevent.VxPublicEvent;

import java.util.List;

public class PublicEventSearchResponse {
    public int totalCount;
    public List<VxPublicEvent> eventList;
}
