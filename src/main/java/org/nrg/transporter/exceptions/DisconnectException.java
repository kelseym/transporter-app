package org.nrg.transporter.exceptions;

import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;

public class DisconnectException extends Exception {
    public DisconnectException(ServerSession session, String message) throws IOException {
     try {

         session.disconnect(1, message);
        } catch (IOException e) {
            throw new IOException(e + "\n" + message);
        }
    }
}
