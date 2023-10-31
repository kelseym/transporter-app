package org.nrg.transporter.mina;

import org.apache.sshd.server.command.Command;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.channel.ChannelSession;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.PayloadService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class CustomScpCommandFactory extends ScpCommandFactory{

    private final TransporterService transporterService;

    @Autowired
    public CustomScpCommandFactory(TransporterService transporterService) {
        super();
        this.transporterService = transporterService;
    }

    @Override
    public Command createCommand(ChannelSession channelSession, String command) throws IOException {
        ServerSession serverSession = channelSession.getServerSession();

        XnatUserSession xnatUserSession = serverSession.getAttribute(SessionAttributes.XNAT_USER_SESSION);


        List<String> requestedSnapshotLabels = resolveValidRequests(command, xnatUserSession);
        if (requestedSnapshotLabels.isEmpty()) {
            throw new IOException("No valid snapshots requested");
        }

        List<Payload> requestedSnapshots = new ArrayList<Payload>(requestedSnapshotLabels.size());
        for (String snapshotLabel : requestedSnapshotLabels) {
            transporterService.getPayload(xnatUserSession, snapshotLabel).ifPresent(requestedSnapshots::add);
        }
        serverSession.setAttribute(SessionAttributes.REQUESTED_SNAPSHOTS, requestedSnapshots);

        command = reformatCommand(command);
        serverSession.setAttribute(SessionAttributes.COMMAND, command);
        return new ScpCommandFactory().createCommand(channelSession, command);
    }

    private List<String> resolveValidRequests(String command, XnatUserSession xnatUserSession) throws IOException {
        List<String> availablePayloadLabels = transporterService.getAvailablePayloadLabels(xnatUserSession);
        List<String> requestedSnapshots = transporterService.parseRequestedSnapshotLabels(command);
        return requestedSnapshots == null ? Collections.emptyList() :
                requestedSnapshots.stream().filter(availablePayloadLabels::contains).collect(Collectors.toList());

    }

    private String reformatCommand(String command) throws IOException {
        if (command == null) {
            throw new IOException("Command is null");
        }
        if (command.isEmpty()) {
            throw new IOException("Command is empty");
        }
        if (!command.startsWith("scp")) {
            throw new IOException("Command is not an scp command");
        }

        return transporterService.stripRequestedSnapshotLabels(command);
    }


}
