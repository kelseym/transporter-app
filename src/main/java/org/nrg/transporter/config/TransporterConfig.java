package org.nrg.transporter.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "transporter")
public class TransporterConfig {
    @Value("@{application.xnat_host:localhost}")
    private String xnatHost;

    @Value("{application.xnat_port:8080}")
    private String xnatPort;

    @Value("{transporter.default_scp_port:22}")
    private String defaultScpPort;

}
