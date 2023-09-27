package org.nrg.transporter.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerStatus {
    private boolean isRunning;
    private int activeConnections;
    private String lastError;
    private SshdConfig sshdConfig;
}
