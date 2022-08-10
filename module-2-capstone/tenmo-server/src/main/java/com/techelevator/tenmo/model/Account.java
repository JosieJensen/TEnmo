package com.techelevator.tenmo.model;

import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


public class Account {


    private Long accountId;
    private Long userId;
    private Double balance;

    public Account() {}

    public Account(Long accountId, Long userId, Double balance) {
        this.accountId = accountId;
        this.userId = userId;
       this.balance = balance;
    }

    //  getters & setters


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}

