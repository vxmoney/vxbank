package eu.vxbank.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProps {

    public List<String> profiles;

    // getter and setter

}