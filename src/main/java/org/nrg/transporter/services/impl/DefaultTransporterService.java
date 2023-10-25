package org.nrg.transporter.services.impl;

import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.model.ServerStatus;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.PayloadService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultTransporterService implements TransporterService {

    RestClientService restClientService;
    AuthenticationService authenticationService;
    ScpServerService scpServerService;
    PayloadService payloadService;
    TransporterConfig transporterConfig;

    @Autowired
    public DefaultTransporterService(RestClientService restClientService, AuthenticationService authenticationService,
                                     ScpServerService scpServerService, PayloadService payloadService, TransporterConfig transporterConfig) {
        this.restClientService = restClientService;
        this.authenticationService = authenticationService;
        this.scpServerService = scpServerService;
        this.payloadService = payloadService;
        this.transporterConfig = transporterConfig;
    }

    // Check connection to XNAT
    @Override
    public Boolean xnatHostStatus() {
        return restClientService.hostStatus();
    }

    //Start default SCP server
    @Override
    public Long startScpServer() throws IOException {
        int defaultPort = Integer.parseInt(transporterConfig.getDefaultScpPort());
        return startScpServer(defaultPort);
    }

    // Start default SCP server
    @Override
    public Long startScpServer(Integer port) throws IOException {
        SshdConfig sshdConfig = SshdConfig.builder()
                .port(port)
                .build();
        return scpServerService.addScpServer(sshdConfig);

    }

    @Override
    public List<ServerStatus> scpServerStatus() {
        return null;
    }

    @Override
    public List<DataSnap> getAvailableSnapshots(String user, String token) {
        return restClientService.getAvailableSnapshots(user, token);
    }

    @Override
    public List<DataSnap> getAvailableSnapshots(XnatUserSession xnatUserSession) {
        return restClientService.getAvailableSnapshots(xnatUserSession);
    }

    @Override
    public Optional<XnatUserSession> getXnatUserSession(String user, String password) {
        return authenticationService.authenticate(user, password);
    }

    @Override
    public List<String> getAvailablePayloadLabels(XnatUserSession xnatUserSession) {
        return payloadService.getAvailablePayloadLabels(xnatUserSession);
    }

    @Override
    public Optional<Payload> getPayload(XnatUserSession xnatUserSession, String label) {
        return payloadService.getPayload(xnatUserSession, label);
    }

}
