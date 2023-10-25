package org.nrg.transporter.config;

import org.mockito.Mockito;
import org.nrg.transporter.mina.SshdPasswordAuthenticator;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.impl.DefaultAuthenticationService;
import org.nrg.transporter.services.impl.DefaultRestClientService;
import org.nrg.transporter.services.impl.DefaultScpServerService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TransporterTestConfig {

    @Profile("mock")
    @Bean
    public AuthenticationService mockAuthenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Profile("mock")
    @Bean
    public ScpServerService mockScpServerService() {
        return new DefaultScpServerService(mockAuthenticationService());
    }

    @Profile("mock")
    @Bean
    public SshdPasswordAuthenticator sshdPasswordAuthenticator() {
        return new SshdPasswordAuthenticator(mockAuthenticationService());
    }

    @Profile("xnat-integration")
    @Bean
    public AuthenticationService authenticationService() {
        RestClientService restClientService = restClientService();
        return new DefaultAuthenticationService(restClientService);
    }

    @Profile("xnat-integration")
    @Bean
    public RestClientService restClientService() {
        TransporterConfig config = new TransporterConfig();
        config.setXnatHost("localhost");
        config.setXnatPort("8080");
        config.setDefaultScpPort("2222");
        return new DefaultRestClientService(config);
    }

    @Profile("xnat-integration")
    @Bean
    public ScpServerService scpServerService() {
        return new DefaultScpServerService(authenticationService());
    }

    @Profile("xnat-integration")
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Profile("xnat-integration")
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

}
