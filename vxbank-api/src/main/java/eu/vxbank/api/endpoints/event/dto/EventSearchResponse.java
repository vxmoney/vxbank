package eu.vxbank.api.endpoints.event.dto;

import vxbank.datastore.data.models.VxEvent;

import java.util.List;

public class EventSearchResponse {
   public int offset;
    public int limit;
    public int totalCount;
    public List<VxEvent> eventList;
}
