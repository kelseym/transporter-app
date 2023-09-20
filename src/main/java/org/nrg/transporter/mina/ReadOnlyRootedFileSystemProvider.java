package org.nrg.transporter.mina;

import org.apache.sshd.common.file.root.RootedFileSystemProvider;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;

public class ReadOnlyRootedFileSystemProvider extends RootedFileSystemProvider {


    public ReadOnlyRootedFileSystemProvider(Path root) throws IOException {
        super.newFileSystem(root, null);
    }


    // Override methods to enforce read-only access
    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void createLink(Path link, Path existing) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean deleteIfExists(Path path) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public FileSystem newFileSystem(Path path, Map<String, ?> env) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        // We only support READ access
        if (modes.length == 1 && modes[0] == AccessMode.READ) {
            super.checkAccess(path, modes);
        } else {
            throw new AccessDeniedException("Only READ access is supported");
        }
    }

}
