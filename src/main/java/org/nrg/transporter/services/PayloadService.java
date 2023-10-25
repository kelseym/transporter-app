package org.nrg.transporter.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PayloadService {

    List<String> getAvailablePayloadLabels(XnatUserSession xnatUserSession);

    List<Payload> getAvailablePayloads(XnatUserSession xnatUserSession);

    Optional<Payload> getPayload(XnatUserSession xnatUserSession, String label);
}
