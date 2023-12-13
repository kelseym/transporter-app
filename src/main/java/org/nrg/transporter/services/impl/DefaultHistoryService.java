package org.nrg.transporter.services.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.server.session.ServerSession;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.HeartbeatService;
import org.nrg.transporter.services.HistoryService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransporterActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.nrg.transporter.mina.SessionAttributes.REQUESTED_SNAPSHOTS;
import static org.nrg.transporter.mina.SessionAttributes.XNAT_USER_SESSION;

@Service
@Slf4j
public class DefaultHistoryService implements HistoryService {


    private final RestClientService restClientService;
    private final HeartbeatService heartbeatService;
    private HistoryQueue historyQueue = new HistoryQueue();

    @Autowired
    public DefaultHistoryService(RestClientService restClientService,
                                 HeartbeatService heartbeatService) {
        this.restClientService = restClientService;
        this.heartbeatService = heartbeatService;
    }

    private void queueHistoryItem(XnatUserSession xnatUserSession,
                                 TransporterActivityItem.TransporterActivityItemCreator historyItem) {
        historyQueue.addHistoryItem(xnatUserSession, historyItem);
    }

    private void sendHistoryItem(XnatUserSession xnatUserSession,
                                TransporterActivityItem.TransporterActivityItemCreator historyItem) {
        queueHistoryItem(xnatUserSession, historyItem);
        flushHistoryItems();
    }

    @Override
    public void queueHistoryItem(Session session, String message) {
        XnatUserSession xnatUserSession = session.getAttribute(XNAT_USER_SESSION);
        TransporterActivityItem.TransporterActivityItemCreator activityItemCreator =
                TransporterActivityItem.TransporterActivityItemCreator.builder()
                .username(session.getUsername())
                .event(message)
                .snapshotId(session.getAttribute(REQUESTED_SNAPSHOTS) != null ?
                        session.getAttribute(REQUESTED_SNAPSHOTS)
                                .stream()
                                .map(Payload::getLabel).collect(Collectors.toList())
                                .toString() : "")
                .remoteAppHeartbeat(getHeartbeat())
                .timestamp(LocalDateTime.now())
                .build();
        historyQueue.addHistoryItem(xnatUserSession, activityItemCreator);
    }

    @Override
    public void queueHistoryItem(Session session, TransporterActivityItem.TransporterActivityItemCreator historyItem) {
        XnatUserSession xnatUserSession = session.getAttribute(XNAT_USER_SESSION);
        historyQueue.addHistoryItem(xnatUserSession, historyItem);
    }

    @Override
    public void sendHistoryItem(Session session, String message) {
        TransporterActivityItem.TransporterActivityItemCreator activityItemCreator =
                TransporterActivityItem.TransporterActivityItemCreator.builder()
                        .username(session.getUsername())
                        .event(message)
                        .snapshotId(session.getAttribute(REQUESTED_SNAPSHOTS) != null ?
                                session.getAttribute(REQUESTED_SNAPSHOTS)
                                        .stream()
                                        .map(Payload::getLabel).collect(Collectors.toList())
                                        .toString() : "")
                        .remoteAppHeartbeat(getHeartbeat())
                        .timestamp(LocalDateTime.now())
                        .build();
        sendHistoryItem(session, activityItemCreator);
    }
    @Override
    public void sendHistoryItem(Session session, TransporterActivityItem.TransporterActivityItemCreator historyItem) {
        XnatUserSession xnatUserSession = session.getAttribute(XNAT_USER_SESSION);
        sendHistoryItem(xnatUserSession, historyItem);
    }

    @Scheduled(fixedRate = 10000) // 10 seconds
    public void postActiveSessionActivity() {
        historyQueue.flushQueue();
    }

    @Override
    public RemoteAppHeartbeat getHeartbeat() {
        return heartbeatService.getHeartbeat();
    }


    @PreDestroy
    public void flushHistoryItems() {
        log.info("Flushing history service items.");
        postActiveSessionActivity();
    }

    @Data
    private class HistoryQueueItem {
        private XnatUserSession xnatUserSession;
        private TransporterActivityItem.TransporterActivityItemCreator historyItem;
        private LocalDateTime timestamp;
        private String messageId;

        public HistoryQueueItem(XnatUserSession xnatUserSession,
                                TransporterActivityItem.TransporterActivityItemCreator historyItem) {
            this.xnatUserSession = xnatUserSession;
            this.historyItem = historyItem;
            this.timestamp = LocalDateTime.now();
            this.messageId = java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }

    protected class HistoryQueue {
        private ArrayList<HistoryQueueItem> historyQueue = new ArrayList<>();

        public synchronized void addHistoryItem(XnatUserSession xnatUserSession,
                                                TransporterActivityItem.TransporterActivityItemCreator activityItem) {
            historyQueue.add(new HistoryQueueItem(xnatUserSession, activityItem));
        }

        public synchronized void flushQueue() {
            if (!historyQueue.isEmpty()) {
                log.info("Flushing history queue.");
                LocalDateTime now = LocalDateTime.now();
                Iterator<HistoryQueueItem> iterator = historyQueue.iterator();

                while (iterator.hasNext()) {
                    HistoryQueueItem historyQueueItem = iterator.next();

                    Boolean success = restClientService.postSessionUpdate(
                            historyQueueItem.getXnatUserSession(),
                            historyQueueItem.getMessageId(),
                            historyQueueItem.getHistoryItem()
                    );
                    if (success) {
                        iterator.remove();
                    } else {
                        log.error("Failed to post history item: {}", historyQueueItem.getHistoryItem());
                        historyQueueItem.setTimestamp(now);
                    }
                }

                historyQueue.removeIf(item -> item.getTimestamp().isBefore(now));
            } else {
                log.debug("History queue is empty.");
            }
        }
    }

}
