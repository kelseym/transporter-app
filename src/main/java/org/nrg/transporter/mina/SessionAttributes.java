package org.nrg.transporter.mina;

import org.apache.sshd.common.AttributeRepository;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.util.List;


public class SessionAttributes {
    public static final AttributeRepository.AttributeKey<String> COMMAND = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<String> TRANSPORT_SESSION_ID = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<XnatUserSession> XNAT_USER_SESSION = new AttributeRepository.AttributeKey<XnatUserSession>();
    public static final AttributeRepository.AttributeKey<List<Payload>> REQUESTED_SNAPSHOTS = new AttributeRepository.AttributeKey<List<Payload>>();
}
