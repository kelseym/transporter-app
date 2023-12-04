package org.nrg.transporter.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.model.ServerStatus;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.*;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DefaultTransporterService implements TransporterService {

    private final RestClientService restClientService;
    private final AuthenticationService authenticationService;
    private final ScpServerService scpServerService;
    private final PayloadService payloadService;
    private final HistoryService historyService;
    private final TransporterConfig transporterConfig;

    private static String SCP_COMMAND_REGEX = "scp.*f\\s+(.+)$";
    private static Pattern SCP_COMMAND_PATTERN = Pattern.compile(SCP_COMMAND_REGEX);



    @Autowired
    public DefaultTransporterService(RestClientService restClientService, AuthenticationService authenticationService,
                                     PayloadService payloadService, HistoryService historyService,
                                     TransporterConfig transporterConfig) {
        this.restClientService = restClientService;
        this.authenticationService = authenticationService;
        this.payloadService = payloadService;
        this.historyService = historyService;
        this.transporterConfig = transporterConfig;
        this.scpServerService = new DefaultScpServerService(authenticationService, this, historyService);
    }

    // Check connection to XNAT
    @Override
    public Boolean xnatHostStatus() {
        return restClientService.hostStatus();
    }


    //Start default SCP server
    @Override
    public Integer startScpServer() throws IOException {
        int defaultPort = Integer.parseInt(transporterConfig.getDefaultScpPort());
        return startScpServer(defaultPort);
    }

    // Start default SCP server
    @Override
    public Integer startScpServer(Integer port) throws IOException {
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

    @Override
    public List<String> parseRequestedSnapshotLabels(String scpCommand) {
        Matcher matcher = SCP_COMMAND_PATTERN.matcher(scpCommand);
        if (matcher.find()) {
            String snapshotLabels = matcher.group(1);
            String[] labelArray = snapshotLabels.split("\\s+");
            return Arrays.asList(labelArray);
        }
        return null;
    }

    @Override
    public String stripRequestedSnapshotLabels(final String scpCommand) {
        Matcher matcher = SCP_COMMAND_PATTERN.matcher(scpCommand);
        if (matcher.find()) {
            return scpCommand.replace(matcher.group(1), "/");
        }
        return scpCommand;
    }

}
