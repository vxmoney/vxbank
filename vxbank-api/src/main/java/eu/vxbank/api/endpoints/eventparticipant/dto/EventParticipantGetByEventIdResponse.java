package eu.vxbank.api.endpoints.eventparticipant.dto;

import vxbank.datastore.data.models.VxEventParticipant;
import vxbank.datastore.data.models.VxEventPayment;

import java.util.List;

public class EventParticipantGetByEventIdResponse {
    public List<VxEventParticipant> participantList;
}
