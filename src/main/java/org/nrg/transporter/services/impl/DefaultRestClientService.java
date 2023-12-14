package org.nrg.transporter.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.RestClientService;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
import org.nrg.xnatx.plugins.transporter.model.Payload;
import org.nrg.xnatx.plugins.transporter.model.RemoteAppHeartbeat;
import org.nrg.xnatx.plugins.transporter.model.TransporterActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DefaultRestClientService implements RestClientService {

    //Load XNAT Host info from properties file.
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private ObjectMapper mapper = new ObjectMapper();

    private final String xnatUrl;
    private static final String LOGIN_URL = "/app/template/Login.vm";
    private static final String DATA_SNAPSHOTS_URL = "/xapi/transporter/snapshots";
    private static final String DATA_PAYLOADS_URL = "/xapi/transporter/payloads";
    private static final String DATA_PAYLOAD_URL = "/xapi/transporter/payload/{label}";
    private static final String XNAT_JSESSION_URL = "/data/JSESSION";
    private static final String XNAT_TOKEN_URL = "/data/services/tokens/issue";
    private static final String HEARTBEAT_URL = "/xapi/transporter/heartbeat";
    private static final String REMOTE_ACTIVITY_URL = "/xapi/transporter/activity";
    private static final ParameterizedTypeReference<List<Payload>> payloadListType = new ParameterizedTypeReference<List<Payload>>() {};
    private static final ParameterizedTypeReference<Payload> payloadType = new ParameterizedTypeReference<Payload>() {};

    private static final ParameterizedTypeReference<List<DataSnap>> dataSnapListType = new ParameterizedTypeReference<List<DataSnap>>() {};

    public DefaultRestClientService(TransporterConfig transporterConfig) {
        this.xnatUrl = addHttp(transporterConfig.getXnatHost());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public Boolean hostStatus() {
        try {
            // Attempt to load XNAT login page to verify connection
            String statusUrl = xnatUrl + LOGIN_URL;

            RestTemplate restTemplate = restTemplateBuilder
                    .defaultHeader("Accept", "application/json")
                    .build();

            ResponseEntity<String> response =
                    restTemplate.getForEntity(statusUrl, String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Throwable e) {
            log.error("Unable to connect to XNAT", e);
        }
        return false;
    }

    @Override
    public void postHeartbeat(XnatUserSession xnatUserSession, RemoteAppHeartbeat heartbeat) {
        if (xnatUserSession != null) {
            postHeartbeat(xnatUserSession.getJsessionid(), heartbeat);
        } else {
            log.debug("No XNAT session found. Skipping heartbeat.");
        }
    }

    private void postHeartbeat(String jsessionid, RemoteAppHeartbeat heartbeat) {
        log.debug("Posting heartbeat to XNAT at " + xnatUrl);
        String heartbeatUrl = xnatUrl + HEARTBEAT_URL + "/" + heartbeat.getRemoteAppId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "JSESSIONID=" + jsessionid);
        HttpEntity<RemoteAppHeartbeat> requestEntity = new HttpEntity<>(heartbeat, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(heartbeatUrl, requestEntity, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.debug("Heartbeat sent successfully");
        } else {
            log.error("Failed to send heartbeat. Status code: " + responseEntity.getStatusCode());
        }
    }

    @Override
    public List<DataSnap> getAvailableSnapshots(String user, String token) {
        // Load available snapshots from XNAT
        String snapshotsUrl = xnatUrl + DATA_SNAPSHOTS_URL;

        RestTemplate restTemplate = restTemplateBuilder
                .basicAuthentication(user, token)
                .defaultHeader("Accept", "application/json")
                .build();

        ResponseEntity<List<DataSnap>> response =
                restTemplate.exchange(snapshotsUrl,
                        HttpMethod.GET,
                        null,
                        dataSnapListType);

        return response.getBody();
    }

    @Override
    public List<DataSnap> getAvailableSnapshots(XnatUserSession xnatUserSession) {
        return getAvailableSnapshots(xnatUserSession.getUsername(), xnatUserSession.getJsessionid());
    }

    @Override
    public List<Payload> getAvailablePayloads(XnatUserSession xnatUserSession) {
        return getAvailablePayloads(xnatUserSession.getJsessionid());
    }

    private List<Payload> getAvailablePayloads(String jsessionid) {
        // Load available payloads from XNAT
        String payloadUrl = xnatUrl + DATA_PAYLOADS_URL;

        RestTemplate restTemplate = restTemplateBuilder
                .defaultHeader("Cookie", "JSESSIONID=" + jsessionid)
                .defaultHeader("Accept", "application/json")
                .build();

        ResponseEntity<List<Payload>> response =
                restTemplate.exchange(payloadUrl,
                        HttpMethod.GET,
                        null,
                        payloadListType);

        return response.getBody();
    }

    @Override
    public Optional<Payload> getPayload(XnatUserSession xnatUserSession, String label) {
        return Optional.ofNullable(getPayload(xnatUserSession.getJsessionid(), label));
    }

    private Payload getPayload(String jsessionid, String label) {
        // Load payload from XNAT
        String payloadUrl = xnatUrl + DATA_PAYLOAD_URL;

        RestTemplate restTemplate = restTemplateBuilder
                .defaultHeader("Cookie", "JSESSIONID=" + jsessionid)
                .defaultHeader("Accept", "application/json")
                .build();

        ResponseEntity<Payload> response =
                restTemplate.exchange(
                        payloadUrl,
                        HttpMethod.GET,
                        null,
                        payloadType,
                        label);

        return response.getBody();
    }

    @Override
    public Optional<XnatUserSession> authenticate(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(xnatUrl + XNAT_JSESSION_URL, HttpMethod.POST, request, String.class);
            if( response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                XnatUserSession xnatUserSession = XnatUserSession.builder()
                        .username(username)
                        .jsessionid(response.getBody())
                        .build();
                return Optional.of(xnatUserSession);
            } else {
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Boolean postSessionUpdate(XnatUserSession xnatUserSession,
                                     String messageId,
                                     TransporterActivityItem.TransporterActivityItemCreator activityItem){
        if (xnatUserSession != null) {
            activityItem.setUsername(xnatUserSession.getUsername());
            return postSessionUpdate(xnatUserSession.getJsessionid(), messageId, activityItem);
        } else {
            log.error("No XNAT session found for user {}. Skipping session activity update.", activityItem.getUsername());
            return false;
        }
    }

    private Boolean postSessionUpdate(String jsessionid,
                                  String messageId,
                                  TransporterActivityItem.TransporterActivityItemCreator activityItem) {
        log.info("Posting session update to XNAT at " + xnatUrl);
        String activityUrl = xnatUrl + REMOTE_ACTIVITY_URL + "/?message_id=" + messageId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "JSESSIONID=" + jsessionid);
        HttpEntity<TransporterActivityItem.TransporterActivityItemCreator> requestEntity =
                new HttpEntity<>(activityItem, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(activityUrl, requestEntity, String.class);
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Status update sent successfully");
            return true;
        } else {
            log.error("Failed to send status update. Status code: " + (responseEntity != null ? responseEntity.getStatusCode() : "null"));
            return false;
        }
    }

    private String addHttp(String xnatHost) {
        if (!xnatHost.startsWith("http://") && !xnatHost.startsWith("https://")) {
            xnatHost = "http://" + xnatHost;
        }
        return xnatHost;
    }}
