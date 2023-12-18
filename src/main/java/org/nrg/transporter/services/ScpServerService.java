package org.nrg.transporter.services;

import org.nrg.transporter.model.SshdConfig;

import java.io.IOException;

public interface ScpServerService {
    Integer addScpServer(SshdConfig sshdConfig)
        throws IOException;

    void removeScpServer(Integer port) throws IOException;
}
