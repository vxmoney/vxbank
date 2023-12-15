package eu.vxbank.api.utils.components.vxintegration;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "application")
@Data
public class ApplicationProps {

    public List<String> profiles;

    // getter and setter

}