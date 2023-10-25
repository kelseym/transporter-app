package org.nrg.transporter.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.transporter.config.TransporterConfig;
import org.nrg.transporter.model.XnatUserSession;
import org.nrg.transporter.services.RestClientService;
import org.nrg.xnatx.plugins.transporter.model.DataSnap;
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
    private static final String XNAT_JSESSION_URL = "/data/JSESSION";
    private static final String XNAT_TOKEN_URL = "/data/services/tokens/issue";


    public DefaultRestClientService(TransporterConfig transporterConfig) {
        this.xnatUrl = addHttp(transporterConfig.getXnatHost()) + ":" + transporterConfig.getXnatPort();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public Boolean hostStatus() {
        // Attempt to load XNAT login page to verify connection
        String statusUrl = xnatUrl + LOGIN_URL;

        RestTemplate restTemplate = restTemplateBuilder
                .defaultHeader("Accept", "application/json")
                .build();

        ResponseEntity<String> response =
                restTemplate.getForEntity(statusUrl, String.class);

        return response.getStatusCode().is2xxSuccessful();
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
                        new ParameterizedTypeReference<List<DataSnap>>() {});

        return response.getBody();
    }

    @Override
    public List<DataSnap> getAvailableSnapshots(XnatUserSession xnatUserSession) {
        return getAvailableSnapshots(xnatUserSession.getUsername(), xnatUserSession.getJsessionid());
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
    public Optional<XnatUserSession> getXnatToken(String username, String password) {
        getXnatToken(username, password, username, 3600);
        return Optional.empty();
    }


    @Override
    public Optional<XnatUserSession> getXnatToken(String username, String password, String alias, int duration) {
        //HttpHeaders headers = new HttpHeaders();
        //headers.setBasicAuth(username, password);
        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //String requestBody = "alias=" + alias + "&duration=" + duration;
        //HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        //try {
        //    XnatUserSession userSession = mapper.readValue(
        //             restTemplate.postForEntity(xnatUrl + XNAT_TOKEN_URL, request, String.class).getBody(),
        //            XnatUserSession.class);
        //    if( response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
        //        XnatUserSession xnatUserSession = XnatUserSession.builder()
        //                .username(username)
        //                .jsessionid(response.getBody())
        //                .build();
        //        return Optional.of(xnatUserSession);
        //    } else {
        //        return Optional.empty();
        //    }
        //} catch (HttpClientErrorException e) {
        //    e.printStackTrace();
        //}
//
//

        return Optional.empty();
    }

    private String addHttp(String xnatHost) {
        if (!xnatHost.startsWith("http://") && !xnatHost.startsWith("https://")) {
            xnatHost = "http://" + xnatHost;
        }
        return xnatHost;
    }}
