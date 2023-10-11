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
import org.nrg.transporter.matchers.HasOneFileMatcher;
import org.nrg.transporter.model.SshdConfig;
import org.nrg.transporter.model.UserConfig;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.ScpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.lang.Thread.sleep;
import static org.mockito.ArgumentMatchers.anyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;


/*
 *  scp -rp -O -P 2222 -o StrictHostKeyChecking=no  -o UserKnownHostsFile=/dev/null admin@localhost:ProjectABC /tmp/
 *
 */

@Slf4j
@ContextConfiguration(classes = {TransporterTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultScpServerServiceTest {

    @Autowired private ScpServerService scpServerService;
    @Autowired private AuthenticationService authenticationService;

    final String TEST_HOST = "localhost";
    final Integer TEST_PORT = 2222;
    final String TEST_USER = "testUser";
    final String TEST_PASS = "testPass";
    final String TEST_SNAPSHOT = "SamplePayload";

    @Before
    public void setUp() throws Exception {

        // Mock the AuthenticationService
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(XnatUserSession.builder().build());
        Path testRootPath = Paths.get(getClass().getClassLoader()
                .getResource("TestRootPath").getPath());
        when(authenticationService.
                resolveRootPath(anyString(), anyString()))
                .thenReturn(testRootPath);

    }

    // Get populated sshdConfig object
    public SshdConfig getPopulateSshdConfig(Integer port) {
        return SshdConfig.builder()
                .port(port)
                .build();
    }

    @Rule
    public TemporaryFolder temporaryFolder = TemporaryFolder.builder().assureDeletion().build();

    //@Test
    //public void testAuthenticateServiceAuth() {
    //    boolean authenticated = authenticationService.authenticate("admin", "password123");
    //    assertThat(authenticated, is(true));
//
    //}

    // Test that the scpServerService is not null
    @Test
    public void testScpServerServiceNotNull() {
        assertThat(scpServerService, is(not(nullValue())));
    }

    // Test that the scpServerService can start a server on port 2222
    @Test
    public void testScpServerServiceStartServer() throws Exception {
        final Integer TEST_PORT = 2222;
        Long serverId = scpServerService.addScpServer(getPopulateSshdConfig(TEST_PORT));
    }

    // Integration test for the SCP server.
    // This test will start a server on port 2222 and attempt to connect to it using the Apache SSHD library.
    // If the connection is successful, the test will pass.
    @Test
    public void testScpService() throws Exception {

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
        } finally {
            client.stop();
        }
    }

    // Integration test for the SCP server.
    // This test will start a server on port 2222 and attempt to download from it using the Apache SSHD library.
    // If the download is successful, the test will pass.
    @Test
    public void testScpDownload() throws Exception {

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