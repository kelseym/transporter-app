package org.nrg.transporter.services.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.nrg.transporter.mina.*;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.HistoryService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.TransporterService;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultScpServerService implements ScpServerService {

    private final AuthenticationService authenticationService;
    private final TransporterService transporterService;
    private final HistoryService historyService;
    private Map<Integer, SshServer> sshdServerMap = new ConcurrentHashMap<>();

    public DefaultScpServerService(AuthenticationService authenticationService,
                                   TransporterService transporterService, HistoryService historyService) {
        this.authenticationService = authenticationService;
        this.transporterService = transporterService;
        this.historyService = historyService;
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
                new SshdPasswordAuthenticator(authenticationService, transporterService, historyService));
        sshdServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        ScpCommandFactory scpCommandFactory = new ScpCommandFactory();
        scpCommandFactory.setDelegateCommandFactory(new CustomScpCommandFactory(transporterService, historyService));
        sshdServer.setCommandFactory(scpCommandFactory);
        sshdServer.setFileSystemFactory(new SnapshotVirtualFileSystemFactory());

        sshdServer.setIoServiceEventListener(new ScpIoEventListener(historyService));
        sshdServer.addSessionListener(new ScpSessionListener(historyService));

        // TODO: Add event listener to SCP server to handle logging
/*        scpCommandFactory.addEventListener(new ScpTransferEventListener() {
            @Override
            public void startFileEvent(Session session, FileOperation op, Path file, long length, Set<PosixFilePermission> perms) throws IOException {
                if (!file.toString().endsWith("catalog.xml")) {
                    ScpTransferEventListener.super.startFileEvent(session, op, file, length, perms);
                } else {
                    System.out.println("Skipping XNAT catalog file: " + file);
                }
            }
        });
*/
        //TODO: Add session log listener
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
