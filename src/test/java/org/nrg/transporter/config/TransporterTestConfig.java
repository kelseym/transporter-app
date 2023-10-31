package org.nrg.transporter.config;

import org.mockito.Mockito;
import org.nrg.transporter.mina.SshdPasswordAuthenticator;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.PayloadService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.transporter.services.impl.DefaultAuthenticationService;
import org.nrg.transporter.services.impl.DefaultPayloadService;
import org.nrg.transporter.services.impl.DefaultRestClientService;
import org.nrg.transporter.services.impl.DefaultScpServerService;
import org.nrg.transporter.services.impl.DefaultTransporterService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.parameters.P;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TransporterTestConfig {

    @Bean
    public TransporterConfig transporterConfig() {
        return new TransporterConfig();
    }

    @Profile("mock")
    @Bean
    public TransporterService mockTransporterService() {
        return new DefaultTransporterService(mockRestClientService(),
                mockAuthenticationService(),
                mockPayloadService(),
                transporterConfig());
    }

    @Profile("mock")
    @Bean
    public RestClientService mockRestClientService() {
        return Mockito.mock(RestClientService.class);
    }

    @Profile("mock")
    @Bean
    public AuthenticationService mockAuthenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Profile("mock")
    @Bean
    public ScpServerService mockScpServerService() {
        return new DefaultScpServerService(mockAuthenticationService(), transporterService());
    }

    @Profile("mock")
    @Bean
    public SshdPasswordAuthenticator sshdPasswordAuthenticator() {
        return new SshdPasswordAuthenticator(mockAuthenticationService());
    }

    @Profile("mock")
    @Bean
    public PayloadService mockPayloadService() {
        return Mockito.mock(PayloadService.class);
    }



    @Profile("xnat-integration")
    @Bean
    public ScpServerService scpServerService() {
        return new DefaultScpServerService(authenticationService(), transporterService());
    }
    @Profile("xnat-integration")
    @Bean
    public TransporterService transporterService() {
        return new DefaultTransporterService(restClientService(),
                authenticationService(),
                payloadService(),
                transporterConfig());
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
        return new DefaultRestClientService(transporterConfig());
    }

    @Profile("xnat-integration")
    @Bean
    public PayloadService payloadService() {
        return new DefaultPayloadService(restClientService());
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
