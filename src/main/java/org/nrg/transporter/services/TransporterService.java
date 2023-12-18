package org.nrg.transporter.services;

import org.nrg.transporter.model.ServerStatus;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TransporterService {
    // Check connection to XNAT
    Boolean xnatHostStatus();

    //Start default SCP server
    Integer startScpServer() throws IOException;

    // Start default SCP server
    Integer startScpServer(Integer port) throws IOException;

    List<ServerStatus> scpServerStatus();

    List<DataSnap> getAvailableSnapshots(String user, String token);

    List<DataSnap> getAvailableSnapshots(XnatUserSession xnatUserSession);

    Optional<XnatUserSession> getXnatUserSession(String user, String password);

    List<String> getAvailablePayloadLabels(XnatUserSession xnatUserSession);

    Optional<Payload> getPayload(XnatUserSession xnatUserSession, String label);

    List<String> parseRequestedSnapshotLabels(final String scpCommand);

    String stripRequestedSnapshotLabels(final String scpCommand);

}
