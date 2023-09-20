package org.nrg.transporter.mina;

import org.apache.sshd.server.command.Command;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.channel.ChannelSession;
import org.nrg.transporter.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class CustomScpCommandFactory extends ScpCommandFactory{

    private final AuthenticationService authenticationService;

    @Autowired
    public CustomScpCommandFactory(AuthenticationService authenticationService) {
        super();
        this.authenticationService = authenticationService;
    }

    @Override
    public Command createCommand(ChannelSession channelSession, String command) throws IOException {
        ServerSession serverSession = channelSession.getServerSession();
        serverSession.setAttribute(SessionAttributes.USERNAME, serverSession.getUsername());
        serverSession.setAttribute(SessionAttributes.COMMAND, command);
        return new ScpCommandFactory().createCommand(channelSession, command);
    }
}
