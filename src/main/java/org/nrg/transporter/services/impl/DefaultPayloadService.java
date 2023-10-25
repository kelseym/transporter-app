package org.nrg.transporter.services.impl;

import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.PayloadService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.xnatx.plugins.transporter.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultPayloadService implements PayloadService {

        private final RestClientService restClientService;

        @Autowired
        public DefaultPayloadService(final RestClientService restClientService) {
                this.restClientService = restClientService;
        }

        @Override
        public List<String> getAvailablePayloadLabels(XnatUserSession xnatUserSession) {
                return getAvailablePayloads(xnatUserSession)
                        .stream().map(Payload::getLabel).collect(Collectors.toList());
        }
        @Override
        public List<Payload> getAvailablePayloads(XnatUserSession xnatUserSession) {
               return restClientService.getAvailablePayloads(xnatUserSession);
        }

        @Override
        public Optional<Payload> getPayload(XnatUserSession xnatUserSession, String label) {
                return restClientService.getPayload(xnatUserSession, label);
        }
}
