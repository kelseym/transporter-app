package org.nrg.transporter.services.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.session.Session;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.HeartbeatService;
import org.nrg.transporter.services.ActivityService;
import org.nrg.transporter.services.RestClientService;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransportActivity.TransportActivityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.nrg.transporter.mina.SessionAttributes.*;

@Service
@Slf4j
public class DefaultActivityService implements ActivityService {


    private final RestClientService restClientService;
    private final HeartbeatService heartbeatService;
    private HistoryQueue historyQueue = new HistoryQueue();

    @Autowired
    public DefaultActivityService(RestClientService restClientService,
                                  HeartbeatService heartbeatService) {
        this.restClientService = restClientService;
        this.heartbeatService = heartbeatService;
    }

    @Override
    public void queueHistoryItem(Session session, String message) {
        String transportSessionId = getTransportSessionId(session);
        XnatUserSession xnatUserSession = session.getAttribute(XNAT_USER_SESSION);
        if(xnatUserSession == null) {
            log.error("XnatUserSession is null");
            return;
        }

        TransportActivityMessage activityItemCreator =
                TransportActivityMessage.builder()
                        .username(session.getUsername())
                        .eventMessage(message)
                        .snapshotId(session.getAttribute(REQUESTED_SNAPSHOTS) != null ?
                            session.getAttribute(REQUESTED_SNAPSHOTS)
                                .stream()
                                .map(Payload::getLabel).collect(Collectors.toList())
                                .toString() : "")
                .sessionId(transportSessionId)
                .timestamp(LocalDateTime.now())
                .build();
        historyQueue.addHistoryItem(xnatUserSession, activityItemCreator);
    }

    @Override
    public void sendHistoryItem(Session session, String message) {
        queueHistoryItem(session, message);
        flushHistoryItems();
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
    private void flushHistoryItems() {
        log.info("Flushing history service items.");
        postActiveSessionActivity();
    }

    @Data
    private class HistoryQueueItem {
        private XnatUserSession xnatUserSession;
        private TransportActivityMessage historyItem;
        private LocalDateTime timestamp;
        private String messageId;

        public HistoryQueueItem(XnatUserSession xnatUserSession,
                                TransportActivityMessage historyItem) {
            this.xnatUserSession = xnatUserSession;
            this.historyItem = historyItem;
            this.timestamp = LocalDateTime.now();
            this.messageId = java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }

    private String getTransportSessionId(Session session) {
        String tsid = session.getAttribute(TRANSPORT_SESSION_ID);
        if (tsid == null) {
            tsid = java.util.UUID.randomUUID().toString().replace("-", "");
            session.setAttribute(TRANSPORT_SESSION_ID, tsid);
        }
        return tsid;
    }

    protected class HistoryQueue {
        private ArrayList<HistoryQueueItem> historyQueue = new ArrayList<>();

        public synchronized void addHistoryItem(XnatUserSession xnatUserSession,
                                                TransportActivityMessage activityItem) {
            historyQueue.add(new HistoryQueueItem(xnatUserSession, activityItem));
        }

        public synchronized void flushQueue() {
            if (!historyQueue.isEmpty()) {
                log.info("Flushing history queue.");
                LocalDateTime now = LocalDateTime.now();
                Iterator<HistoryQueueItem> iterator = historyQueue.iterator();

                Boolean heartbeatSent = false;

                while (iterator.hasNext()) {
                    HistoryQueueItem historyQueueItem = iterator.next();

                    // Send heartbeat to XNAT once per queue flush, using first users session
                    if (!heartbeatSent) {
                        restClientService.postHeartbeat(
                                historyQueueItem.getXnatUserSession(), heartbeatService.getHeartbeat()
                        );
                        heartbeatSent = true;
                    }

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
            }
        }
    }

}
