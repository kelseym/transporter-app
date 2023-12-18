package org.nrg.transporter.mina;

import org.apache.sshd.common.file.root.RootedFileSystemProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;

public class ReadOnlyFileSystemProvider extends RootedFileSystemProvider {

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options) throws IOException {
        throw new IOException("Writing files is not allowed on this file system.");
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new IOException("Deleting is not allowed on this file system.");
    }

    @Override
    public boolean deleteIfExists(Path path) throws IOException {
        throw new IOException("Deleting is not allowed on this file system.");
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new IOException("Directory creation is not allowed on this file system.");
    }
}
