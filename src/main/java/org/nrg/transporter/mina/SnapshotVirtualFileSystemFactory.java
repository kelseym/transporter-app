package org.nrg.transporter.mina;

import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;

public class SnapshotVirtualFileSystemFactory extends VirtualFileSystemFactory {

    @Override
    public FileSystem createFileSystem(SessionContext session) throws IOException {
        //Path dir = getUserHomeDir(session);
        //if (dir == null) {
        //    throw new InvalidPathException(session.getUsername(), "Cannot resolve home directory");
        //}

        return new RootedFileSystemProvider().newFileSystem(
                getRequestedSnapshotDir(session), Collections.emptyMap());
    }

    private Path getRequestedSnapshotDir(SessionContext session) {
        session.getAttribute()
        String requestedSnapshot = session.getUsername();
        return null;
    }

}
