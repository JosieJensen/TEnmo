package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDetails;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TransferServiceREST implements TransferService {
    private String baseUrl = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;

    public TransferServiceREST(String baseUrl, AuthenticatedUser authenticatedUser) {
        this.baseUrl = baseUrl;
        this.authenticatedUser = authenticatedUser;
    }

@Override
    public Transfer createTransfer(Transfer transfer) {

        try {
           transfer = restTemplate.exchange(baseUrl + "transfer/create", HttpMethod.POST, makeAuthEntity(transfer), Transfer.class).getBody();
        } catch(RestClientResponseException e) {
            if (e.getMessage().contains("We know capitalism is hard. You don't have any money. :( ")) {
                System.out.println("It looks like you don't have enough money for that transfer, babe.");
            }
        }
        return transfer;
    }

    @Override
    public Transfer[] getAllTransfers() {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(baseUrl + "transfers/", HttpMethod.GET,
                    makeAuthEntity(), Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return transfers;
    }

    public Transfer getTransferById(AuthenticatedUser authenticatedUser, Long transferId) {
        Transfer transfer = null;
        HttpEntity entity = makeAuthEntity();
        try {
            transfer = restTemplate.exchange(baseUrl + "transfer/" + transferId,
                    HttpMethod.GET, entity, Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return transfer;
    }

    public Transfer[] getTransfersByAccountId(Long accountId) {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(baseUrl + "transfers/" + "account/" + accountId,
                    HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return transfers;
    }

    public Transfer[] getTransfersByUserId(Long userId) {
        Transfer[] transfers = null;
        HttpEntity entity = makeAuthEntity();
        try {
            transfers = restTemplate.exchange(baseUrl + "transfers/" + "user/" + userId,
                    HttpMethod.GET, entity, Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("We could not complete this request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("We could complete this request due to a network error. Please try again.");
        }
        return transfers;
    }

   public TransferDetails[] getAllTransferDetails(Long id) {
        TransferDetails[] transfers = null;
        try {
            ResponseEntity<TransferDetails[]> response =
                    restTemplate.exchange(baseUrl + "transferdetails/" + id, HttpMethod.GET,
                            makeAuthEntity(), TransferDetails[].class);
            transfers = response.getBody();

        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public void updateTransfer(Transfer transfer, Long transferId) {

        try {
            transfer = restTemplate.exchange(baseUrl + "transfer/update/" + transferId, HttpMethod.PUT, makeAuthEntity(transfer), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeAuthEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer,headers);
    }

}
