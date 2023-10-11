package org.nrg.transporter.services.impl;

import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.AuthenticationService;
import org.nrg.transporter.services.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class DefaultAuthenticationService implements AuthenticationService {

    RestClientService restClientService;

    @Autowired
    public DefaultAuthenticationService(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @Override
    public XnatUserSession authenticate(String username, String password) {
        //TODO: Call XNAT to authenticate user
        return null;
    }

    @Override
    public Path resolveRootPath(String username, String scpRequest) {
        //TODO: Call XNAT to resolve user root directory
        return null;
    }


}
