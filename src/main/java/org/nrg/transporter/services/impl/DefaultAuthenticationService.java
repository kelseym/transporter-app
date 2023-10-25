package org.nrg.transporter.services.impl;

import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
public class DefaultAuthenticationService implements AuthenticationService {

    RestClientService restClientService;

    @Autowired
    public DefaultAuthenticationService(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @Override
    public Optional<XnatUserSession> authenticate(String username, String password) {
        return restClientService.authenticate(username, password);
    }

}
