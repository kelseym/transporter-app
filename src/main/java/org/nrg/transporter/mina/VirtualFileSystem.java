package org.nrg.transporter.mina;

import org.apache.sshd.common.file.util.BaseFileSystem;
import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Set;

public class VirtualFileSystem extends BaseFileSystem<VirtualPath> {

    protected VirtualFileSystem(FileSystemProvider fileSystemProvider) {
        super(fileSystemProvider);
    }


    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return null;
    }

    @Override
    public Path getPath(String first, String... more) {
        //TODO: Implement a custom path resolver that will only return the files in the manifest.
        Path path = delegate.getPath(first, more);
        Path virtualPath = path.resolve(first);
        // Map the virtual path back to the real path
        Path realPath = Paths.get("TEST123");/* logic to resolve real path based on virtualPath */;

        return new VirtualPath(this, realPath, virtualPath);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return null;
    }

    @Override
    protected VirtualPath create(String root, List<String> names) {
        return null;
    }
}


