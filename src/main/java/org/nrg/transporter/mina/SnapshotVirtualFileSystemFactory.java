package org.nrg.transporter.mina;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SnapshotVirtualFileSystemFactory extends VirtualFileSystemFactory {


    @Override
    public FileSystem createFileSystem(SessionContext session) throws IOException {

        RootedFileSystemProvider rootedFileSystemProvider = new ReadOnlyFileSystemProvider();

        FileSystem fileSystem = rootedFileSystemProvider.newFileSystem(
                getRequestedSnapshotDir(session), Collections.emptyMap());
        log.info("Created file system: {}", fileSystem);

        return fileSystem;
    }

    private Path getRequestedSnapshotDir(SessionContext session) {
        List<Payload> requestedSnapshots = session.getAttribute(SessionAttributes.REQUESTED_SNAPSHOTS);

        String snapshotPath = null;
        if (requestedSnapshots.size()>0){
            Payload snapshot = requestedSnapshots.get(0);
            if (snapshot.getType().equals(Payload.Type.DIRECTORY)) {
                if (snapshot.getFileManifests().size()>0){
                    snapshotPath = snapshot.getFileManifests().get(0).getPath();
                }
            }
        }
        return Paths.get(snapshotPath);
    }

}
