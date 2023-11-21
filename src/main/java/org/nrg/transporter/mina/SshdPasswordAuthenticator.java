package org.nrg.transporter.mina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static org.nrg.transporter.mina.SessionAttributes.XNAT_USER_SESSION;

@Component
@Scope("prototype")
public class SshdPasswordAuthenticator implements PasswordAuthenticator {

    private final AuthenticationService authenticationService;
    private final TransporterService transporterService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter objectWriter;

    @Autowired
    public SshdPasswordAuthenticator(AuthenticationService authenticationService,
                                     TransporterService transporterService) {
        this.authenticationService = authenticationService;
        this.transporterService = transporterService;
        mapper.registerModule(new JavaTimeModule());
        objectWriter = mapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) {
        XnatUserSession xnatUserSession = null;
        try {
            xnatUserSession = getXnatSession(username, password).orElse(null);
        } catch (Exception e) {
        }
        if(xnatUserSession != null) {
            session.setAttribute(XNAT_USER_SESSION, xnatUserSession);
            return true;
        } else {
            try {
                String failureMessage = "Connection error.";
                failureMessage += transporterService.xnatHostStatus() ?
                        "\nXNAT authentication failed for user: " + username :
                        "\nXNAT host is not available. Check configuration.\n" +
                                objectWriter.writeValueAsString(transporterService.getHeartbeat());
                session.disconnect(1, failureMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    }


    private Optional<XnatUserSession> getXnatSession(String username, String password) {
        return authenticationService.authenticate(username, password);
    }

}
