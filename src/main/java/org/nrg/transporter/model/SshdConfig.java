package org.nrg.transporter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SshdConfig implements Serializable{

    private static final long serialVersionUID = 1L;

    private int port; // Port on which the SSHD server will listen
    private Path hostKeyPath; // Path to the host key file

}
