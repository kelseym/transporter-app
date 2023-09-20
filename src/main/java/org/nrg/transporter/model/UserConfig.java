package org.nrg.transporter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserConfig implements Serializable {

    private String username;
    private String password; // Consider storing a hashed version for security
    private List<String> authorizedKeys; // List of authorized public keys for the user
    private boolean allowPasswordAuthentication;
}
