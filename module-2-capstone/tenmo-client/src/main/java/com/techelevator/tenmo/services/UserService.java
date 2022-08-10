package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class UserService {

    private String baseUrl = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser user;

    public UserService(String baseUrl, AuthenticatedUser user) {
        this.baseUrl = baseUrl;
        this.user = user;
    }

    public User[] getAllUsers(AuthenticatedUser authenticatedUser) {
        User[] users = null;
        try {
            users = restTemplate.exchange(baseUrl + "/users", HttpMethod.GET, makeAuthEntity(),
                    User[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return users;
    }

    public User getByUserId(Long userId) {
        User user = null;
        try {
            user = restTemplate.exchange(baseUrl + "/user/" + userId, HttpMethod.GET,
                    makeAuthEntity(), User.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return user;
    }

    public User getByUserByUsername(String username) {
       User user = new User();
        try {
            user = restTemplate.exchange(baseUrl + "user/" + username, HttpMethod.GET,
                    makeAuthEntity(user), User.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return user;
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<User> makeAuthEntity(User authUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(authUser,headers);
    }


}
