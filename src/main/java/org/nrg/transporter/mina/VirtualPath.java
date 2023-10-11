package org.nrg.transporter.mina;

import org.apache.sshd.common.file.util.BasePath;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VirtualPath extends BasePath<VirtualPath, VirtualFileSystem> {

    public VirtualPath(VirtualFileSystem fileSystem, String root, List<String> names) {
        super(fileSystem, root, names);
    }


    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return null;
    }
}
