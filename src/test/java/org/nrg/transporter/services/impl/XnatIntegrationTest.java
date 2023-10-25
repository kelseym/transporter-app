package org.nrg.transporter.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.SshException;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nrg.transporter.config.TransporterTestConfig;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.ScpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/*
 *  scp -rp -O -P 2222 -o StrictHostKeyChecking=no  -o UserKnownHostsFile=/dev/null admin@localhost:ProjectABC /tmp/
 *
 */

@Slf4j
@ContextConfiguration(classes = {TransporterTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("xnat-integration")
public class XnatIntegrationTest {

    @Autowired private ScpServerService scpServerService;

    final String TEST_HOST = "localhost";
    final Integer TEST_PORT = 2222;
    final String TEST_USER = "admin";
    final String TEST_PASS = "admin";
    final String TEST_SNAPSHOT = "SamplePayload";

    @Before
    public void setUp() throws Exception {

    }

    public SshdConfig getPopulateSshdConfig(Integer port) {
        return SshdConfig.builder()
                .port(port)
                .build();
    }

    @Rule
    public TemporaryFolder temporaryFolder = TemporaryFolder.builder().assureDeletion().build();

    // XNAT integration test for the SCP server.
    // This test will start a server on port 2222 and attempt to connect to XNAT using the Apache SSHD library.
    @Test
    public void testXnatConnection() throws Exception {

        final File downloadDir = temporaryFolder.newFolder("download");

        Long serverId = scpServerService.addScpServer(getPopulateSshdConfig(TEST_PORT));
        assertThat(serverId, is(not(nullValue())));

        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try (ClientSession session = client.connect(TEST_USER, TEST_HOST, TEST_PORT).verify().getSession()) {
            session.addPasswordIdentity(TEST_PASS);
            boolean success = false;
            try {
                success = session.auth().verify().isSuccess();
            } catch (SshException e) {}
            assertThat("Failed to authenticate SCP session", success, is(true));

            ScpClientCreator creator = ScpClientCreator.instance();
            ScpClient scpClient = creator.createScpClient(session);
            scpClient.download(TEST_SNAPSHOT, downloadDir.getAbsolutePath());
            //assertThat(Paths.get(downloadDir.getAbsolutePath()), HasOneFileMatcher.hasOneFile());
        } finally {
            client.stop();
        }
    }


}
