package eu.vxbank.api.endpoints.event.dto;

import vxbank.datastore.data.models.VxEvent;

import java.util.List;

public class EventSearchResponse {
    int offset;
    int limit;
    int totalCount;
    List<VxEvent> eventList;
}
