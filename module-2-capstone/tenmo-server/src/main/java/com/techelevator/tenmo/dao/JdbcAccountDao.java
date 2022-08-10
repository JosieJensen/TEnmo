package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.serverexceptions.InsufficientFunds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Double getBalanceByUserId(Long userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        Double balance = null;
        try {
            balance = jdbcTemplate.queryForObject(sql, Double.class, userId);
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
        return balance;
    }

    @Override
    public Double getBalanceByAccountId(Long accountId) {
        String sql = "SELECT balance FROM account WHERE account_id = ?;";
        Double balance = null;
        try {
            balance = jdbcTemplate.queryForObject(sql, Double.class, accountId);
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
        return balance;
    }

    @Override
    public Account getAccountByAccountId(Long accountId) {
        String sql = "SELECT * FROM account where account_id = ?; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        Account account = null;
        while (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(Long userId) {
        String sql = "SELECT * FROM account where user_id = ?";
        Account account = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
        return account;
    }

    @Override
    public Account[] findAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * from account; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }
        return accounts.toArray(new Account[0]);
    }

    @Override
    public Account depositAccount(Account account, Long id, Double amount)  {
         account.setBalance(account.getBalance() + amount);
         String sql = "UPDATE account SET balance = ? WHERE user_id = ?; ";
         jdbcTemplate.update(sql, account.getBalance(), id);
        return account;

    }

    @Override
    public Account withdrawAccount(Account account, Long id, Double amount) {
        account.setBalance(account.getBalance() - amount);
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?; ";
        jdbcTemplate.update(sql, account.getBalance(), id);
        return account;
    }


    private Account mapRowToAccount (SqlRowSet results){
            Account account = new Account();
            account.setBalance(results.getDouble("balance"));
            account.setAccountId(results.getLong("account_id"));
            account.setUserId(results.getLong("user_id"));
            return account;
        }

    }



