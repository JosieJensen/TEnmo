package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;

public class AuthenticationService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthenticationService(String url) {
        this.baseUrl = url;
    }

    public AuthenticatedUser login(UserCredentials credentials) {
        HttpEntity<UserCredentials> entity = createCredentialsEntity(credentials);
        AuthenticatedUser user = null;
        try {
            ResponseEntity<AuthenticatedUser> response =
                    restTemplate.exchange(baseUrl + "login", HttpMethod.POST, entity, AuthenticatedUser.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public boolean register(UserCredentials credentials) {
        HttpEntity<UserCredentials> entity = createCredentialsEntity(credentials);
        boolean success = false;
        try {
            restTemplate.exchange(baseUrl + "register", HttpMethod.POST, entity, Void.class);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

//    public AuthenticatedUser getAuthUserById() {
//        AuthenticatedUser user = null;
//        try {
//            user = restTemplate.exchange(baseUrl + "/user/" + userId, HttpMethod.GET,
//                    makeAuthEntity(), User.class).getBody();
//        } catch (RestClientResponseException e) {
//            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
//        } catch (ResourceAccessException e) {
//            System.out.println("We could complete this request due to a network error. Please try again.");
//        }
//        return user;return authenticatedUser; }

    private HttpEntity<UserCredentials> createCredentialsEntity(UserCredentials credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(credentials, headers);
    }
}
