package org.nrg.transporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class TransporterApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(TransporterApplication.class, args);

        // SCP Command from MACOS
        // scp -P 22 -rp -O  -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null admin@localhost:/ ./
    }

}
