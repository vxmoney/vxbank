package eu.vxbank.api.utils.components.vxintegration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "vx-integration-config")
@Data
public class VxIntegrationConfig {
    public List<VxIntegration> vxIntegrationList;
}
