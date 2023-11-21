package org.nrg.transporter.mina;

import org.apache.sshd.server.command.Command;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.channel.ChannelSession;
import org.nrg.transporter.exceptions.DisconnectException;
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
        try {
            ServerSession serverSession = channelSession.getServerSession();
            XnatUserSession xnatUserSession = serverSession.getAttribute(SessionAttributes.XNAT_USER_SESSION);

            List<String> requestedPayloadLabels = validatePayloadRequests(command, serverSession, xnatUserSession);

            List<Payload> requestedPayloads = new ArrayList<Payload>(requestedPayloadLabels.size());
            for (String snapshotLabel : requestedPayloadLabels) {
                transporterService.getPayload(xnatUserSession, snapshotLabel).ifPresent(requestedPayloads::add);
            }
            serverSession.setAttribute(SessionAttributes.REQUESTED_SNAPSHOTS, requestedPayloads);

            command = reformatCommand(serverSession, command);
            serverSession.setAttribute(SessionAttributes.COMMAND, command);
            return new ScpCommandFactory().createCommand(channelSession, command);
        } catch (DisconnectException e) {
            throw new IOException(e);
        }
    }

    private List<String> validatePayloadRequests(String command, ServerSession session, XnatUserSession xnatUserSession)
            throws IOException, DisconnectException {
        List<String> availablePayloadLabels = transporterService.getAvailablePayloadLabels(xnatUserSession);
        List<String> requestedSnapshots = transporterService.parseRequestedSnapshotLabels(command);
        List<String> validSnapshotLabels =
                requestedSnapshots == null ?
                        Collections.emptyList() :
                        requestedSnapshots.stream()
                                .filter(availablePayloadLabels::contains).collect(Collectors.toList());

        if (validSnapshotLabels.isEmpty()) {
            String errorMessage = "No valid snapshots requested.";
            errorMessage += "\nRequested snapshots: " + requestedSnapshots;
            errorMessage += "\nAvailable snapshots: " + availablePayloadLabels;
            throw new DisconnectException(session, errorMessage);
        }
        else {
            return validSnapshotLabels;
        }
    }

    private String reformatCommand(ServerSession session, String command) throws DisconnectException, IOException {
        if (command == null) {
            throw new DisconnectException(session, "Command is null");
        }
        if (command.isEmpty()) {
            throw new DisconnectException(session, "Command is empty");
        }
        
        if (!command.startsWith("scp")) {
            throw new DisconnectException(session, "Command is not an scp command");
        }
        return transporterService.stripRequestedSnapshotLabels(command);
    }


}
