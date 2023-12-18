package org.nrg.transporter.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.session.Session;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.ActivityService;

import static org.nrg.transporter.mina.SessionAttributes.XNAT_USER_SESSION;

@Slf4j
public class ScpSessionListener implements SessionListener {

    private final ActivityService activityService;

    public ScpSessionListener(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public void sessionCreated(Session session) {

        log.info("Session created: {}", session);
    }

    @Override
    public void sessionEvent(Session session, Event event) {
        log.info("Session event: {}", event);
        queueActivityMessage(session, event.toString());
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
            activityService.sendHistoryItem(session, message);
    }

    private void queueActivityMessage(Session session, String message) {
        XnatUserSession xnatUserSession = session.getAttribute(XNAT_USER_SESSION);
        if (xnatUserSession != null) {
            activityService.queueHistoryItem(session, message);
        }
    }

}
