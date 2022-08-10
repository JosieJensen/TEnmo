package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;

import java.math.BigDecimal;

public interface AccountService {

    Double getBalance();
    Account getAccountByAccountId(AuthenticatedUser authenticatedUser, Long accountId);
    Account getAccountByUserId(Long userId);
    Account depositAccount(Account account, Long id, Double amount);
    Account withdrawAccount(Account account, Long id, Double amount);
}
