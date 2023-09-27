package org.nrg.transporter.services.impl;

import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.model.ServerStatus;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.PayloadService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
    public List<DataSnap> getDataSnaps(String user, String token) {
        return restClientService.getAvailableSnapshots(user, token);
    }

}
