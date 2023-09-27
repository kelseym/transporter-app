package org.nrg.transporter.services;

import org.nrg.transporter.model.ServerStatus;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;

import java.io.IOException;
import java.util.List;

public interface TransporterService {
    // Check connection to XNAT
    Boolean xnatHostStatus();

    //Start default SCP server
    Long startScpServer() throws IOException;

    // Start default SCP server
    Long startScpServer(Integer port) throws IOException;

    List<ServerStatus> scpServerStatus();

    List<DataSnap> getDataSnaps(String user, String token);
}
