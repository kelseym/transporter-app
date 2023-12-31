package org.nrg.transporter.services;

import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransportActivity.TransportActivityMessage;

import java.util.List;
import java.util.Optional;

public interface RestClientService {

    public Boolean hostStatus();

    public void postHeartbeat(XnatUserSession xnatUserSession, RemoteAppHeartbeat heartbeat);

    public List<DataSnap> getAvailableSnapshots(String user, String token);

    List<DataSnap> getAvailableSnapshots(XnatUserSession xnatUserSession);

    List<Payload> getAvailablePayloads(XnatUserSession xnatUserSession);

    Optional<Payload> getPayload(XnatUserSession xnatUserSession, String label);

    public Optional<XnatUserSession> authenticate(String username, String password);

    Boolean postSessionUpdate(XnatUserSession xnatUserSession,
                           String messageId,
                           TransportActivityMessage historyItem);
}
