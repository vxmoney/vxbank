package eu.vxbank.api.endpoints.event.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventJoinResponse {
    public Long vxUserId;
    public Long eventId;
    public Integer eventActiveParticipantsCount;
    public String eventTitle;
}
