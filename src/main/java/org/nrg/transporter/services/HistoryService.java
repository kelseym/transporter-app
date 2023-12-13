package org.nrg.transporter.services;

import org.apache.sshd.common.session.Session;
import org.apache.sshd.server.session.ServerSession;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransporterActivityItem;

public interface HistoryService {


    void queueHistoryItem(Session session, String message);

    void queueHistoryItem(Session session,
                          TransporterActivityItem.TransporterActivityItemCreator historyItem);

    void sendHistoryItem(Session session, String message);

    void sendHistoryItem(Session session,
                         TransporterActivityItem.TransporterActivityItemCreator historyItem);

    RemoteAppHeartbeat getHeartbeat();
}
