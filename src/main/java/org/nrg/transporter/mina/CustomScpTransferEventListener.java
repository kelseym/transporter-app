package org.nrg.transporter.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.scp.common.ScpTransferEventListener;
import org.nrg.transporter.services.HistoryService;

import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

@Slf4j
public class CustomScpTransferEventListener implements ScpTransferEventListener {

    private final HistoryService historyService;

    public CustomScpTransferEventListener(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public void startFileEvent(Session session, FileOperation op, Path file, long length, Set<PosixFilePermission> perms) {
        log.debug("Started download: " + file.toString());
        historyService.queueHistoryItem(session, "Started download: " + file.getFileName());
    }

    @Override
    public void endFileEvent(Session session, FileOperation op, Path file, long length, Set<PosixFilePermission> perms, Throwable thrown) {
        if (thrown == null) {
            historyService.queueHistoryItem(session, "Finished download: " + file.getFileName());
            log.debug("Finished download: " + file.getFileName());
        } else {
            historyService.queueHistoryItem(session, "File download failed: " + file.getFileName());
            log.error("File download failed: " + file.toString());
            log.error(thrown.getMessage());
        }
    }

}

