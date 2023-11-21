package org.nrg.transporter.services;

import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.springframework.scheduling.annotation.Scheduled;

public interface HeartbeatService {
    void initialize();

    @Scheduled(fixedRate = 60000) // 60 seconds
    void runPeriodicTask();

    RemoteAppHeartbeat getHeartbeat();
}
