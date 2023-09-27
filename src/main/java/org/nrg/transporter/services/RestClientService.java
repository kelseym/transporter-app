package org.nrg.transporter.services;

import org.nrg.transporter.model.XnatUserSession;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;

import java.util.List;
import java.util.Optional;

public interface RestClientService {

    public Boolean hostStatus();

    public List<DataSnap> getAvailableSnapshots(String user, String token);

    public Optional<XnatUserSession> authenticate(String username, String password);


    Optional<XnatUserSession> getXnatToken(String username, String password);

    Optional<XnatUserSession> getXnatToken(String username, String password, String alias, int duration);
}
