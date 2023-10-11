package org.nrg.transporter.mina;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;

import org.apache.sshd.common.util.ValidateUtils;
import org.nrg.transporter.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;


@Component
@Scope("prototype")
public class CustomVirtualFileSystemFactory extends VirtualFileSystemFactory {

    private final AuthenticationService authenticationService;

    //TODO: Modify to provide a file system view based on the manifest.
    // When a client connects and requests files, the file system factory
    // should only expose the files from the manifest.

    @Autowired
    public CustomVirtualFileSystemFactory(AuthenticationService authenticationService) {
        super();
        this.authenticationService = authenticationService;
    }

    @Override
    public Path getUserHomeDir(SessionContext session) {
        String userName = session.getUsername();
        ValidateUtils.checkNotNullAndNotEmpty(userName, "No username");

        return authenticationService.resolveRootPath(userName, session.getAttribute(SessionAttributes.COMMAND));

    }

    @Override
    public FileSystem createFileSystem(SessionContext session) {
        return new VirtualFileSystem(FileSystems.getDefault(), null);
    }
}