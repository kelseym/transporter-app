package org.nrg.transporter.mina;

import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.nrg.transporter.mina.SessionAttributes.XNAT_USER_SESSION;

@Component
@Scope("prototype")
public class SshdPasswordAuthenticator implements PasswordAuthenticator {

    @Autowired
    public SshdPasswordAuthenticator(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private AuthenticationService authenticationService;

    @Override
    public boolean authenticate(String username, String password, ServerSession session) {
        XnatUserSession xnatUserSession = getXnatSession(username, password).orElse(null);
        if(xnatUserSession != null) {
            session.setAttribute(XNAT_USER_SESSION, xnatUserSession);
            return true;
        }
        return false;
    }


    private Optional<XnatUserSession> getXnatSession(String username, String password) {
        return authenticationService.authenticate(username, password);
    }

}
