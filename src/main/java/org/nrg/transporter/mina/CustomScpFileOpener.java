package org.nrg.transporter.mina;

import org.apache.sshd.common.session.Session;
import org.apache.sshd.scp.common.helpers.DefaultScpFileOpener;
import org.nrg.transporter.services.TransporterService;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

public class CustomScpFileOpener extends DefaultScpFileOpener {

    private final TransporterService transporterService;
    private final List<String> excludeList;

    public CustomScpFileOpener(TransporterService transporterService) {
        super();
        this.transporterService = transporterService;
        this.excludeList = transporterService.getExcludeList();
    }

    @Override
    public boolean sendAsRegularFile(Session session, Path path, LinkOption... options)
            throws IOException {
        return Files.isRegularFile(path, options) && !isExcluded(path);
    }

    @Override
    public boolean sendAsDirectory(Session session, Path path, LinkOption... options)
            throws IOException {
        return Files.isDirectory(path, options) && !isExcluded(path);
    }

    private boolean isExcluded(Path path) {
        if (path == null || excludeList == null || excludeList.isEmpty()) {
            return false;
        } else {
            return excludeList.stream().anyMatch(exclude -> path.toString().matches(exclude));
        }
    }
}
