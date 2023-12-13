package org.nrg.transporter.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.session.Session;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.HistoryService;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.nrg.xnatx.plugins.transporter.model.TransporterActivityItem;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.nrg.transporter.mina.SessionAttributes.REQUESTED_SNAPSHOTS;
import static org.nrg.transporter.mina.SessionAttributes.XNAT_USER_SESSION;

@Slf4j
public class ScpSessionListener implements SessionListener {

    private final HistoryService historyService;

    public ScpSessionListener(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public void sessionCreated(Session session) {

        log.info("Session created: {}", session);
        session.getAttribute(XNAT_USER_SESSION);

    }

    @Override
    public void sessionEvent(Session session, Event event) {
        log.info("Session event: {}", event);
        queueActivityHistory(session, event);
    }

    @Override
    public void sessionException(Session session, Throwable t) {
        log.error("Session exception: {}", t.getMessage());
        queueActivityMessage(session, "SCP exception: " + t.getMessage());
    }

    @Override
    public void sessionClosed(Session session) {
        log.info("Session closed: {}", session);
        sendActivityHistory(session, "SCP session closed");
    }


    private void sendActivityHistory(Session session, String message) {
            historyService.sendHistoryItem(session, message);
    }

    private void queueActivityHistory(Session session, Event event) {
        queueActivityMessage(session, event.toString());
    }

    private void queueActivityMessage(Session session, String message) {
        XnatUserSession xnatUserSession = session.getAttribute(XNAT_USER_SESSION);
        if (xnatUserSession != null) {
            historyService.queueHistoryItem(session, message);
        }
    }

}
