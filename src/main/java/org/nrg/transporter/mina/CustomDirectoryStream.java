package org.nrg.transporter.mina;

import org.nrg.xnatx.plugins.transporter.model.Payload;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CustomDirectoryStream implements DirectoryStream<Path> {

    private final Payload payload;

    public CustomDirectoryStream(Payload payload) {
        this.payload = payload;
    }

    @Override
    public Iterator<Path> iterator() {
        return payload.getFileManifests().stream()
                .map(Payload.FileManifest::getSnapshotPath)
                .map(f -> Paths.get(f))
                .iterator();
    }

    @Override
    public void close() throws IOException {
        // Implement any cleanup logic if necessary
    }
}

