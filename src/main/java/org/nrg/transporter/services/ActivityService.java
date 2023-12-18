package org.nrg.transporter.services;

import org.apache.sshd.common.session.Session;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransporterActivityItem;

public interface ActivityService {


    void queueHistoryItem(Session session, String message);

    void sendHistoryItem(Session session, String message);

    RemoteAppHeartbeat getHeartbeat();
}
