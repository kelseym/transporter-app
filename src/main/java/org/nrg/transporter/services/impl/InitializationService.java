package org.nrg.transporter.services.impl;

import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class InitializationService implements ApplicationRunner {

    TransporterService transporterService;
    TransporterConfig transporterConfig;

    @Autowired
    public InitializationService(TransporterService transporterService, TransporterConfig transporterConfig) {
        this.transporterService = transporterService;
        this.transporterConfig = transporterConfig;
    }

    public void initialize() {
        try {

            transporterService.startScpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }
}
