package org.nrg.transporter.services.impl;

import org.nrg.transporter.services.AuthenticationService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DefaultAuthenticationService implements AuthenticationService {


    @Override
    public boolean authenticate(String username, String password) {
        //TODO: Call XNAT to authenticate user
        return false;
    }

    @Override
    public Path resolveRootPath(String username, String scpRequest) {
        //TODO: Call XNAT to resolve user root directory
        return null;
    }


}
