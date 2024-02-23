package eu.vxbank.api.endpoints.eventparticipant.dto;

import vxbank.datastore.data.models.VxEventParticipant;
import vxbank.datastore.data.models.VxEventPayment;
import vxbank.datastore.data.models.VxUser;

import java.util.List;

public class EventParticipantGetByEventIdResponse {
    public List<VxEventParticipant> participantList;
    public List<VxUser> vxUserList;
}
