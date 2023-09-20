package org.nrg.transporter.mina;

import com.sun.source.doctree.AttributeTree;
import org.apache.sshd.common.AttributeRepository;


public class SessionAttributes {
    public static final AttributeRepository.AttributeKey<String> USERNAME = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<String> COMMAND = new AttributeRepository.AttributeKey<String>();
    public static final AttributeRepository.AttributeKey<String> SNAPSHOT = new AttributeRepository.AttributeKey<String>();
}
