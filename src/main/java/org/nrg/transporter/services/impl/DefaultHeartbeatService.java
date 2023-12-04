package org.nrg.transporter.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.services.HeartbeatService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.transporter.services.TransporterService;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@Slf4j
public class DefaultHeartbeatService implements HeartbeatService {

    private final RestClientService restClientService;
    private final TransporterConfig transporterConfig;
    private RemoteAppHeartbeat heartbeat;

    public DefaultHeartbeatService(final RestClientService restClientService,
                                   final TransporterConfig transporterConfig) {
        this.restClientService = restClientService;
        this.transporterConfig = transporterConfig;
    }
    @Override
    public void initialize() {
        refreshHeartbeat();
    }

    @Override
    public RemoteAppHeartbeat getHeartbeat() {
        return heartbeat;
    }

    @Scheduled(fixedRate = 60000) // 60 seconds
    public void refreshHeartbeat() {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (Throwable e) {
            log.error("Unable to get host address", e);
        }
        Boolean xnatConnectionStatus = restClientService.hostStatus();
        heartbeat =  RemoteAppHeartbeat.builder()
                .status(xnatConnectionStatus ? "OK" : "ERROR")
                .message(xnatConnectionStatus ? "XNAT Connection OK" : "XNAT Connection Error")
                .remoteAppId(transporterConfig.getRemoteAppId())
                .remoteHost(host)
                .xnatHost(transporterConfig.getXnatHost() + ":" + transporterConfig.getXnatPort())
                .xnatConnectionStatus(xnatConnectionStatus ? "Connected" : "Disconnected")
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

}
