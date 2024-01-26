package org.nrg.transporter.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "transporter")
@EnableScheduling
public class TransporterConfig {
    private String remoteAppId = "TestInstance";

    private String xnatHost = "localhost";

    private String xnatPort = "8080";

    private String defaultScpPort = "22";

    private String scpHostKeyPath = "/tmp/hostKeyFile";

    private String excludeList = ".*catalog.xml,.DS_Store";
}
