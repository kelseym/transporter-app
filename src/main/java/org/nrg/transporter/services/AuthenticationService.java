package org.nrg.transporter.services;

import org.nrg.transporter.model.XnatUserSession;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
public interface AuthenticationService {

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true if authentication is successful, false otherwise.
     */
    Optional<XnatUserSession> authenticate(String username, String password);


    /**
     * Resolve user root directory based on username and SCP directory request.
     *
     */
    Path resolveRootPath(String username, String scpRequest);
}