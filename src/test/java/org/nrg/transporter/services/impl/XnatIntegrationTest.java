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
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.PayloadService;
import org.nrg.transporter.services.ScpServerService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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
    @Autowired private AuthenticationService authenticationService;
    @Autowired private PayloadService payloadService;
    @Autowired private TransporterService transporterService;

    final String TEST_HOST = "localhost";
    final Integer TEST_PORT = 2222;
    final String TEST_USER = "admin";
    final String TEST_PASS = "admin";
    final String TEST_SNAPSHOT = "SamplePayload";

    private XnatUserSession userSession;

    @Before
    public void setUp() throws Exception {
        userSession = authenticationService.authenticate(TEST_USER, TEST_PASS).get();

        // mount /data/xnat/build according to transporter config

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

        Integer serverId = scpServerService.addScpServer(getPopulateSshdConfig(TEST_PORT));
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


    private List<String> getPayloadLabels() {
        List<String> availablePayloadLabels = payloadService.getAvailablePayloadLabels(userSession);
        assertThat(availablePayloadLabels.size(), greaterThan(0));
        return availablePayloadLabels;
    }

    @Test
    public void getUserSession() throws Exception {
        assertThat(userSession, is(true));
    }
    @Test
    public void testPayloadServiceListing() throws Exception {
        List<String> availablePayloadLabels = getPayloadLabels();
        assertThat(availablePayloadLabels.size(), greaterThan(0));
    }

    @Test
    public void testPayloadServiceGetDirectory() throws Exception {
        String payloadLabel = getPayloadLabels().get(0);
        Optional<Payload> payload = payloadService.getPayload(userSession, payloadLabel);
        assertThat(payload.isPresent(), is(true));
        assertThat(payload.get().getType(), is(Payload.Type.DIRECTORY));
    }

    @Test
    public void testPayloadServiceQuery() throws Exception {
        String payloadLabel = getPayloadLabels().get(0);
        Optional<Payload> payload = payloadService.getPayload(userSession, payloadLabel);
        assertThat(payload.isPresent(), is(true));
        assertThat(payload.get().getType(), is(Payload.Type.DIRECTORY));
    }

    @Test
    public void testScpCommandValidation() throws Exception {
        String payloadLabel = getPayloadLabels().get(0);

        // Use the payload label to get the payload directory
        transporterService.startScpServer(TEST_PORT);
    }

}
