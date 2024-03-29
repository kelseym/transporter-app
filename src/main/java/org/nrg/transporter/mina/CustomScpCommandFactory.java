package org.nrg.transporter.mina;

import org.apache.sshd.scp.common.ScpFileOpener;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.channel.ChannelSession;
import org.nrg.transporter.exceptions.DisconnectException;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.ActivityService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomScpCommandFactory extends ScpCommandFactory{

    private final TransporterService transporterService;
    private final ActivityService activityService;

    public CustomScpCommandFactory(TransporterService transporterService, ActivityService activityService) {
        super();
        this.transporterService = transporterService;
        this.activityService = activityService;
        addEventListener(new CustomScpTransferEventListener(activityService));
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

            if (this.isSupportedCommand(channelSession, command)){
                return executeSupportedCommand(channelSession, command);
            }
            else{
                disconnectWithMessage(serverSession, "Unsupported command: " + command);
                throw new DisconnectException(serverSession, "Unsupported command: " + command);
            }
        } catch (DisconnectException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ScpFileOpener getScpFileOpener() {
        return new CustomScpFileOpener(transporterService);
    }

    private List<String> validatePayloadRequests(String command, ServerSession session, XnatUserSession xnatUserSession)
            throws IOException, DisconnectException {
        List<String> availablePayloadLabels = transporterService.getAvailablePayloadLabels(xnatUserSession);
        log.info("Available snapshots: {}", availablePayloadLabels);
        List<String> requestedSnapshots = transporterService.parseRequestedSnapshotLabels(command);
        if (requestedSnapshots!= null && requestedSnapshots.size()>1){
            String errorMessage = "Multiple snapshots requested. Only one snapshot can be requested at a time.";
            errorMessage += "\nRequested snapshots: " + requestedSnapshots;
            errorMessage += "\nAvailable snapshots: " + availablePayloadLabels;
            log.error(errorMessage);
            disconnectWithMessage(session, errorMessage);
        }
        log.info("Requested snapshots: {}", requestedSnapshots);

        List<String> validSnapshotLabels =
                requestedSnapshots == null ?
                        Collections.emptyList() :
                        requestedSnapshots.stream()
                                .filter(availablePayloadLabels::contains).collect(Collectors.toList());

        if (validSnapshotLabels.isEmpty()) {
            String errorMessage = "No valid snapshots requested.";
            errorMessage += "\nRequested snapshots: " + requestedSnapshots;
            errorMessage += "\nAvailable snapshots: " + availablePayloadLabels;
            log.error(errorMessage);
            disconnectWithMessage(session, errorMessage);
        }
        return validSnapshotLabels;
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

    private void disconnectWithMessage(ServerSession session, String message) throws IOException {
        log.error(message);
        activityService.queueHistoryItem(session,message);
        session.disconnect(1, message);
    }


}
