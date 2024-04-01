package eu.vxbank.api.endpoints.publicevent.clinetpayment;

import eu.vxbank.api.endpoints.publicevent.publicevent.dto.PublicEventGetManagerListResponse;
import eu.vxbank.api.utils.components.SystemService;
import eu.vxbank.api.utils.components.VxStripeKeys;
import eu.vxbank.api.utils.components.vxintegration.VxIntegrationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publicEventClientPayment")
public class PublicEventClientPaymentEndpoint {

    @Autowired
    SystemService systemService;

    @Autowired
    VxStripeKeys stripeKeys;

    @Autowired
    private VxIntegrationConfig vxIntegrationConfig;

    @GetMapping("/getClientReport/event/{eventId}/client/{clientId}")
    @ResponseBody
    public PublicEventGetManagerListResponse getClientReport(Authentication auth, @PathVariable Long eventId, @PathVariable Long clientId) {
        throw new IllegalStateException("Please implement this");
    }
}
