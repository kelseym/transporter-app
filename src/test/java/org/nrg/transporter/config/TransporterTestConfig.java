package org.nrg.transporter.config;

import org.mockito.Mockito;
import org.nrg.transporter.mina.SshdPasswordAuthenticator;
import org.nrg.transporter.services.*;
import org.nrg.transporter.services.impl.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
                mockHistoryService(),
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
        return new DefaultScpServerService(mockAuthenticationService(), mockTransporterService(), mockHistoryService(),transporterConfig());
    }

    @Profile("mock")
    @Bean
    public SshdPasswordAuthenticator sshdPasswordAuthenticator() {
        return new SshdPasswordAuthenticator(mockAuthenticationService(), mockTransporterService(), mockHistoryService());
    }

    @Profile("mock")
    @Bean
    public PayloadService mockPayloadService() {
        return Mockito.mock(PayloadService.class);
    }

    @Profile("mock")
    @Bean
    public HeartbeatService mockHeartbeatService() {
        return Mockito.mock(HeartbeatService.class);
    }

    @Profile("mock")
    @Bean
    public ActivityService mockHistoryService() {
        return Mockito.mock(ActivityService.class);
    }


    @Profile("xnat-integration")
    @Bean
    public ScpServerService scpServerService() {
        return new DefaultScpServerService(authenticationService(), transporterService(), historyService(), transporterConfig());
    }

    @Profile("xnat-integration")
    @Bean
    public TransporterService transporterService() {
        return new DefaultTransporterService(restClientService(),
                authenticationService(),
                payloadService(),
                historyService(),
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

    @Profile("xnat-integration")
    @Bean
    public HeartbeatService heartbeatService() {
        return new DefaultHeartbeatService(restClientService(), transporterConfig());
    }

    @Profile("xnat-integration")
    @Bean
    public ActivityService historyService() {
        return new DefaultActivityService(restClientService(), heartbeatService());
    }
}