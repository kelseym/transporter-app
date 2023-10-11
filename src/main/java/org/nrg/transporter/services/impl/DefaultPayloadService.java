package org.nrg.transporter.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.transporter.services.PayloadService;
import org.nrg.xnatx.plugins.transporter.model.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class DefaultPayloadService implements PayloadService {

        @Override
        public List<Payload> getPayloads(String user) {
                return Arrays.asList(loadPayloadFromFile("payload.json"));
        }

        @Override
        public Payload getPayload(String user, String id) {
                return loadPayloadFromFile("payload.json");
        }


        // Test code to load payload from file
        private Payload loadPayloadFromFile(String filename) {
                Payload payload = null;
                try {
                        File file = new File(filename);
                        ObjectMapper mapper = new ObjectMapper();
                        payload = mapper.readValue(file, Payload.class);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return payload;
        }

}
