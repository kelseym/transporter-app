package org.nrg.transporter.services;

import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransporterActivityItem;

public interface HistoryService {


    void queueHistoryItem(XnatUserSession xnatUserSession,
                          TransporterActivityItem.TransporterActivityItemCreator historyItem);

    void sendHistoryItem(XnatUserSession xnatUserSession,
                         TransporterActivityItem.TransporterActivityItemCreator historyItem);

    RemoteAppHeartbeat getHeartbeat();
}
