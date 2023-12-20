package eu.vxbank.api.endpoints.ping.dto;

import eu.vxbank.api.endpoints.user.dto.Funds;

import java.util.List;
import java.util.Optional;

public class PingRequestFundsResponse {

    public Long userId;
    public List<Funds> fundsList;

}
