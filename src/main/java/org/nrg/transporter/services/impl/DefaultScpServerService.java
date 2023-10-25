package org.nrg.transporter.services.impl;


import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.nrg.transporter.mina.CustomScpCommandFactory;
import org.nrg.transporter.mina.SnapshotVirtualFileSystemFactory;
import org.nrg.transporter.mina.SshdPasswordAuthenticator;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.ScpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DefaultScpServerService implements ScpServerService {

    private SshServer sshdServer;
    private AuthenticationService authenticationService;

    @Autowired
    public DefaultScpServerService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Long addScpServer(SshdConfig sshdConfig) throws IOException {
        sshdServer = SshServer.setUpDefaultServer();
        sshdServer.setPort(sshdConfig.getPort());
        sshdServer.setPasswordAuthenticator(new SshdPasswordAuthenticator(authenticationService));
        sshdServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshdServer.setCommandFactory(new CustomScpCommandFactory(authenticationService));
        sshdServer.setFileSystemFactory(new SnapshotVirtualFileSystemFactory());

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

        //TODO: Add server ID to XNAT via passthrough service
        //TODO: Add session log listener
        sshdServer.start();

        //TODO: return the id of the scp server
        return 0L;
    }


}
