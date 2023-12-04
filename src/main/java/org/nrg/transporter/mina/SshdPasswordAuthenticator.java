package org.nrg.transporter.mina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.nrg.transporter.exceptions.DisconnectException;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.HistoryService;
import org.nrg.transporter.services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static org.nrg.transporter.mina.SessionAttributes.XNAT_USER_SESSION;

public class SshdPasswordAuthenticator implements PasswordAuthenticator {

    private final AuthenticationService authenticationService;
    private final TransporterService transporterService;
    private final HistoryService historyService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter objectWriter;

    public SshdPasswordAuthenticator(AuthenticationService authenticationService,
                                     TransporterService transporterService,
                                     HistoryService historyService) {
        this.authenticationService = authenticationService;
        this.transporterService = transporterService;
        this.historyService = historyService;
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
                                objectWriter.writeValueAsString(historyService.getHeartbeat());
                throw new DisconnectException(session, failureMessage);
            } catch (DisconnectException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private Optional<XnatUserSession> getXnatSession(String username, String password) {
        return authenticationService.authenticate(username, password);
    }

}
