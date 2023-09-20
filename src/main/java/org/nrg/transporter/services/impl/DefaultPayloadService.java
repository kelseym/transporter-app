package org.nrg.transporter.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.transporter.model.Payload;
import org.nrg.transporter.services.PayloadService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultPayloadService implements PayloadService {



        @Override
        public List<Payload> getPayloads(String user) {
                return null;
        }

        @Override
        public Payload getPayload(String user, String id) {
                return null;
        }


        // Test code to load payload from file
        private Payload loadPayloadFromFile(String filename) throws IOException {
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
