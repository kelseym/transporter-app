package org.nrg.transporter.mina;

import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.scp.common.ScpFileOpener;
import org.apache.sshd.scp.common.ScpTransferEventListener;
import org.apache.sshd.scp.server.ScpCommand;
import org.apache.sshd.server.channel.ChannelSession;

public class CustomScpCommand extends ScpCommand {
    /**
     * @param channelSession  The {@link ChannelSession} through which the command was received
     * @param command         The command to be executed
     * @param executorService An {@link CloseableExecutorService} to be used when
     *                        {@code start(ChannelSession, Environment)}-ing execution. If {@code null} an ad-hoc
     *                        single-threaded service is created and used.
     * @param sendSize        Size (in bytes) of buffer to use when sending files
     * @param receiveSize     Size (in bytes) of buffer to use when receiving files
     * @param fileOpener      The {@link ScpFileOpener} - if {@code null} then {@link DefaultScpFileOpener} is used
     * @param eventListener   An {@link ScpTransferEventListener} - may be {@code null}
     * @see ThreadUtils#newSingleThreadExecutor(String)
     * @see ScpHelper#MIN_SEND_BUFFER_SIZE
     * @see ScpHelper#MIN_RECEIVE_BUFFER_SIZE
     */
    public CustomScpCommand(ChannelSession channelSession, String command, CloseableExecutorService executorService, int sendSize, int receiveSize, ScpFileOpener fileOpener, ScpTransferEventListener eventListener) {
        super(channelSession, command, executorService, sendSize, receiveSize, fileOpener, eventListener);
    }
}
