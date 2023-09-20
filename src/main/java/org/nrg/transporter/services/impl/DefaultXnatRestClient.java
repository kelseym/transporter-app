package org.nrg.transporter.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.nrg.transporter.services.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultXnatRestClient implements RestClientService {

    //TODO: Load XNAT URL from properties file.
    private final String XNAT_BASE_URL = "https://your-xnat-url";
    private final RestTemplate restTemplate;

    @Autowired
    public DefaultXnatRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public ResponseEntity<String> authenticateUser(String username, String password) {
        String authUrl = XNAT_BASE_URL + "/path-to-auth-endpoint";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(authUrl, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> getUserConfiguration(String username, String sessionToken) {
        String configUrl = XNAT_BASE_URL + "/path-to-config-endpoint/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(sessionToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(configUrl, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> postStatusUpdate(String statusMessage, String sessionToken) {
        String statusUrl = XNAT_BASE_URL + "/path-to-status-endpoint";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(sessionToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(statusMessage, headers);
        return restTemplate.exchange(statusUrl, HttpMethod.POST, entity, String.class);
    }
}
