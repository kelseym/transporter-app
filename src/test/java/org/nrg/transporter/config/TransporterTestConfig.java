package org.nrg.transporter.config;

import org.mockito.Mockito;
import org.nrg.transporter.mina.SshdPasswordAuthenticator;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.impl.DefaultAuthenticationService;
import org.nrg.transporter.services.impl.DefaultScpServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransporterTestConfig {

    @Bean
    public AuthenticationService mockAuthenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Bean
    public ScpServerService scpServerService() {
        return new DefaultScpServerService(mockAuthenticationService());
    }

    @Bean
    public SshdPasswordAuthenticator sshdPasswordAuthenticator() {
        return new SshdPasswordAuthenticator(mockAuthenticationService());
    }

}
