package org.nrg.transporter.services.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.mina.*;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.ActivityService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.TransporterService;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultScpServerService implements ScpServerService {

    private final AuthenticationService authenticationService;
    private final TransporterService transporterService;
    private final ActivityService activityService;
    private final TransporterConfig transporterConfig;
    private Map<Integer, SshServer> sshdServerMap = new ConcurrentHashMap<>();

    public DefaultScpServerService(AuthenticationService authenticationService,
                                   TransporterService transporterService, ActivityService activityService,
                                   TransporterConfig transporterConfig) {
        this.authenticationService = authenticationService;
        this.transporterService = transporterService;
        this.activityService = activityService;
        this.transporterConfig = transporterConfig;
    }

    @Override
    public Integer addScpServer(SshdConfig sshdConfig) throws IOException {
        // If the server is already running, stop it
        if (sshdServerMap.containsKey(sshdConfig.getPort())) {
            removeScpServer(sshdConfig.getPort());
        }


        SshServer sshdServer = SshServer.setUpDefaultServer();
        sshdServer.setPort(sshdConfig.getPort());
        sshdServer.setPasswordAuthenticator(
                new SshdPasswordAuthenticator(authenticationService, transporterService, activityService));
        sshdServer.setKeyPairProvider(
                new SimpleGeneratorHostKeyProvider(Paths.get(transporterConfig.getScpHostKeyPath())));
        CustomScpCommandFactory customScpCommandFactory = new CustomScpCommandFactory(transporterService, activityService);
        sshdServer.setCommandFactory(customScpCommandFactory);
        sshdServer.setFileSystemFactory(new SnapshotVirtualFileSystemFactory());
        sshdServer.setIoServiceEventListener(new ScpIoEventListener(activityService));
        sshdServer.addSessionListener(new ScpSessionListener(activityService));
        sshdServer.start();

        sshdServerMap.put(sshdConfig.getPort(), sshdServer);
        return sshdServer.getPort();
    }

    @Override
    public void removeScpServer(Integer port) throws IOException {
        SshServer sshdServer = sshdServerMap.get(port);
        if (sshdServer != null) {
            sshdServer.stop();
            sshdServerMap.remove(port);
        }
    }

    @PreDestroy
    public void removeScpServers() throws IOException {
        for (Integer port : sshdServerMap.keySet()) {
            removeScpServer(port);
        }
    }

}
