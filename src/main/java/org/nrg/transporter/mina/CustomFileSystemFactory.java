package org.nrg.transporter.mina;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;

import org.apache.sshd.common.util.ValidateUtils;
import org.nrg.transporter.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;


@Component
@Scope("prototype")
public class CustomFileSystemFactory extends VirtualFileSystemFactory {

    private final AuthenticationService authenticationService;

    @Autowired
    public CustomFileSystemFactory(AuthenticationService authenticationService) {
        super();
        this.authenticationService = authenticationService;
    }

    @Override
    public Path getUserHomeDir(SessionContext session) {
        String userName = session.getUsername();
        ValidateUtils.checkNotNullAndNotEmpty(userName, "No username");

        return authenticationService.resolveRootPath(userName, session.getAttribute(SessionAttributes.COMMAND));

    }
}