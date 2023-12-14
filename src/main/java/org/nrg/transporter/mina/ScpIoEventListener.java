package org.nrg.transporter.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.io.IoAcceptor;
import org.apache.sshd.common.io.IoConnector;
import org.apache.sshd.common.io.IoServiceEventListener;
import org.nrg.transporter.services.ActivityService;

import java.io.IOException;
import java.net.SocketAddress;

@Slf4j
public class ScpIoEventListener implements IoServiceEventListener {

    private final ActivityService activityService;

    public ScpIoEventListener(ActivityService activityService) {
        this.activityService = activityService;
    }
    @Override
    public void connectionEstablished(IoConnector connector, SocketAddress local, AttributeRepository context, SocketAddress remote) throws IOException {
        log.debug("Connection established: {}", remote);
    }

    @Override
    public void abortEstablishedConnection(IoConnector connector, SocketAddress local, AttributeRepository context, SocketAddress remote, Throwable reason) throws IOException {
        log.debug("Connection aborted: {}", remote);
    }

    @Override
    public void connectionAccepted(IoAcceptor acceptor, SocketAddress local, SocketAddress remote, SocketAddress service) throws IOException {
        log.debug("Connection accepted: {}", remote);
    }

    @Override
    public void abortAcceptedConnection(IoAcceptor acceptor, SocketAddress local, SocketAddress remote, SocketAddress service, Throwable reason) throws IOException {
        log.debug("Connection aborted: {}", remote);
    }
}
