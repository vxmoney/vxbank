package eu.vxbank.api.endpoints.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TokenInfo {
    public String firebaseId;
    public String email;
    public Long expiresAt;
    public String vxToken;
}
