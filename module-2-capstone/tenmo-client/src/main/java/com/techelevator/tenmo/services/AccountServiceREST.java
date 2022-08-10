package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.util.BasicLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountServiceREST implements AccountService {

    private String baseUrl = "http://localhost8080";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;


    public AccountServiceREST(String baseUrl, AuthenticatedUser authenticatedUser) {
        this.baseUrl = baseUrl;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public Double getBalance() {
        HttpEntity entity = makeAuthEntity();
       Double balance = null;
        try {
            balance = restTemplate.exchange(baseUrl + "/balance", HttpMethod.GET, entity, Double.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return balance;
    }

    @Override
    public Account getAccountByAccountId(AuthenticatedUser authenticatedUser, Long accountId) {
        HttpEntity entity = new HttpEntity<>(authenticatedUser);
        Account account = null;
        try {
            account = restTemplate.exchange(baseUrl + "/account/" + accountId, HttpMethod.GET, entity, Account.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(Long userId) {
        HttpEntity entity = new HttpEntity<>(authenticatedUser);
        Account account = null;
//        try {
            account = restTemplate.exchange(baseUrl + "/account/" + "user/" + userId, HttpMethod.GET, entity, Account.class).getBody();
//        } catch (RestClientResponseException e) {
//            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
//        } catch (ResourceAccessException e) {
//            System.out.println("We could complete this request due to a network error. Please try again.");
//        }
        return account;
    }

    @Override
    public Account depositAccount(Account account, Long id, Double amount) {

        HttpEntity entity = makeAuthEntity(account);
//        try {
            account = restTemplate.exchange(baseUrl + "/deposit/user/" + id + "/" + amount, HttpMethod.PUT, entity, Account.class).getBody();
//        } catch (RestClientResponseException e) {
//            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
//        } catch (ResourceAccessException e) {
//            System.out.println("We could complete this request due to a network error. Please try again.");
//        }
        return account;
    }

    @Override
    public Account withdrawAccount(Account account, Long id, Double amount) {
        HttpEntity entity = makeAuthEntity(account);
//        try {
            account = restTemplate.exchange(baseUrl + "withdrawal/user/" + id + "/" + amount, HttpMethod.PUT, entity, Account.class).getBody();
//        } catch (RestClientResponseException e) {
//            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
//        } catch (ResourceAccessException e) {
//            System.out.println("We could complete this request due to a network error. Please try again.");
//        }
        return account;
    }

    public Account[] listAccounts(){
        Account[] accounts = null;
        try{
            ResponseEntity<Account[]> response =
                    restTemplate.exchange(baseUrl +"accounts/",
                            HttpMethod.GET,
                            makeAuthEntity(),
                            Account[].class);
            accounts=response.getBody();
        }catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accounts;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Account> makeAuthEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(account,headers);
    }
}
