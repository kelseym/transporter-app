package org.nrg.transporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class XnatUserSession {

    // Assuming XNAT sessions require a session ID or token.
    @JsonProperty("id")
    private String aliasId;

    @JsonProperty("xnatUserId")
    private String username;
    private String alias;
    private String secret;
    private String jsessionid;
    @JsonProperty("created")
    private long aliasTime;
    @JsonProperty("estimatedExpirationTime")
    private long aliasExpiryTime;
    private String ipAddress;
    private String details;

    // toString() method for debugging
    @Override
    public String toString() {
        return "XnatUserSession{" +
                "aliasId='" + aliasId + '\'' +
                ", username='" + username + '\'' +
                ", alias='" + alias + '\'' +
                ", jsessionid='" + jsessionid + '\'' +
                ", aliasTime=" + aliasTime +
                ", aliasExpiryTime=" + aliasExpiryTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}