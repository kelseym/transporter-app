package org.nrg.transporter.mina;

import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        XnatUserSession xnatUserSession = isUserValid(username, password);
        if(xnatUserSession != null) {
            setHomeDirectory(username, session);
            return true;
        }
        return false;
    }


    private XnatUserSession isUserValid(String username, String password) {
        return authenticationService.authenticate(username, password);
    }

    private void setHomeDirectory(String username, ServerSession session) {
        // TODO: Set home directory for user
    }

}
