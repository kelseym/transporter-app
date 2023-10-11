package org.nrg.transporter.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PayloadService {

    // Query XNAT for available payload/snapshot objects
    // Return a list of payload/snapshot objects
    public List<Payload> getPayloads(String user);

    // Return Paylod object with specified ID
    public Payload getPayload(String user, String id);


}
