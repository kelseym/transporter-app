package org.nrg.transporter.mina;

import com.sun.source.doctree.AttributeTree;
import org.apache.sshd.common.AttributeRepository;
import org.nrg.transporter.model.XnatUserSession;


public class SessionAttributes {
    public static final AttributeRepository.AttributeKey<String> USERNAME = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<String> COMMAND = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<String> SNAPSHOT_DIR = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<XnatUserSession> XNAT_USER_SESSION = new AttributeRepository.AttributeKey<XnatUserSession>();
}
