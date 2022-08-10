package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    Double getBalanceByUserId(Long userId);
    Double getBalanceByAccountId(Long accountId);
    Account getAccountByAccountId(Long accountId);
    Account getAccountByUserId(Long userId);
    Account[] findAllAccounts();
    Account depositAccount(Account account, Long id, Double amount);
    Account withdrawAccount(Account account, Long accountId, Double amount);
}
