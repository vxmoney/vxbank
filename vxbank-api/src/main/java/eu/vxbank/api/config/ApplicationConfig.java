package eu.vxbank.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final VxUserDetailsService vxUserDetailsService;

    @Bean
    public UserDetailsService userDetailsService() {
        return vxUserDetailsService;
    }

    //1:37:00
    //https://youtu.be/KxqlJblhzfI?si=fjHu_gbTGzlgFNtQ&t=5705
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }
}
