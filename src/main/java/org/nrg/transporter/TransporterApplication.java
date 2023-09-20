package org.nrg.transporter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class TransporterApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(TransporterApplication.class, args);

        // SCP Command from MACOS
        // scp -P 22 -rp -O  -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/nulladmin@localhost:/ ./
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
}
