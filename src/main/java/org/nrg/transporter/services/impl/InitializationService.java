package org.nrg.transporter.services.impl;

import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.services.HeartbeatService;
import org.nrg.transporter.services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InitializationService implements ApplicationRunner {

    TransporterService transporterService;
    TransporterConfig transporterConfig;
    HeartbeatService heartbeatService;

    @Autowired
    public InitializationService(TransporterService transporterService,
                                 TransporterConfig transporterConfig,
                                 HeartbeatService heartbeatService) {
        this.transporterService = transporterService;
        this.transporterConfig = transporterConfig;
        this.heartbeatService = heartbeatService;
    }


    public void initialize() {
    try {

        transporterService.startScpServer();
        heartbeatService.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }
}
