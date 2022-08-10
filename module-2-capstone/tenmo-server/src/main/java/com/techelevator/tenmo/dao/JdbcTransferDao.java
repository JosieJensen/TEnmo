package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDetails;
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
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createTransfer(Transfer transfer, Long transferTypeId, Long transferStatusId, Long accountTo, Long accountFrom, Double amount) {

        String sql = " INSERT INTO transfer (transfer_type_id, transfer_status_id, account_to, account_from, amount)" +
                " VALUES(?,?,?,?,?);";

         jdbcTemplate.update(sql, transferTypeId, transferStatusId, accountTo,
                   accountFrom, amount);
//       } catch (DataAccessException e) {
//           System.out.println("Error accessing database");
//       }
    }

    public void createTrans(Transfer transfer) {
        String sql = " INSERT INTO transfer (transfer_type_id, transfer_status_id, account_to, account_from, amount)" +
                " VALUES(?,?,?,?,?);";

        jdbcTemplate.update(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountTo(),
                transfer.getAccountFrom(), transfer.getAmount());
    }



    @Override
    public Transfer[] getAllTransfers() {
        String sql = " SELECT * FROM transfer;";
        List<Transfer> transfers = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
        return transfers.toArray(new Transfer[0]);
    }

    @Override
    public Transfer getTransferById(Long transferId) {
        String sql = " SELECT * from transfer WHERE transfer_id = ?; ";
        Transfer transfer = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByUserId(Long userId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_from OR account.account_id = transfer.account_to " +
                "WHERE (account_from IN (SELECT account_id FROM account WHERE user_id = ? )) OR (account_to IN (SELECT account_id FROM account WHERE user_id = ? ))";
        List<Transfer> transfers = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
            return transfers;
        }

    public Transfer[] getTransfersByAccountId(Long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers " +
                "JOIN accounts ON accounts.account_id = transfers.account_from  " +
                "WHERE account_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
        return transfers.toArray(new Transfer[0]);
    }

    @Override
    public TransferDetails[] getTransferDetails(Long id) {
        List<TransferDetails> transfers = new ArrayList<>();

        String SQL = "SELECT  t.transfer_id, tu.username as account_from , tu2.username as account_to, " +
                "tt.transfer_type_desc as transfer_type, ts.transfer_status_desc as transfer_status, t.amount as amount FROM transfer t " +
                "INNER JOIN account ac ON account_from =ac.account_id " +
                "INNER JOIN account ac2 on account_to = ac2.account_id " +
                "INNER JOIN tenmo_user tu ON tu.user_id = ac.user_id " +
                "INNER JOIN tenmo_user tu2 ON tu2.user_id = ac2.user_id " +
                "INNER JOIN transfer_type tt ON tt.transfer_type_id = t.transfer_type_id " +
                "INNER JOIN transfer_status ts on ts.transfer_status_id = t.transfer_status_id " +
                "WHERE (account_from IN (SELECT account_id FROM account WHERE user_id = ?)) " +
                "OR (account_to IN (SELECT account_id FROM account WHERE user_id = ?));";
        SqlRowSet results = jdbcTemplate.queryForRowSet(SQL, id, id);
        while(results.next()){
            TransferDetails transfer = mapToRowTransferDetails(results);
            transfers.add(transfer);
        }
        return transfers.toArray(new TransferDetails[0]);
    }

    @Override
    public List<Transfer> getPendingTransfers(Long userId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfers.transfer_status_id, account_from, account_to, amount " +
                "FROM transfers " +
                "JOIN accounts ON accounts.account_id = transfers.account_from " +
                "JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
                "WHERE user_id = ? AND transfer_status_desc = 'Pending'";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        List<Transfer> transfers = new ArrayList<>();

        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public void updateTransfer(Transfer transfer, Long transferStatusID, Long transferTypeId, Long transferId) {
        String sql = "UPDATE transfer SET transfer_status_id = ?, transfer_type_id = ? WHERE transfer_id = ?; ";

        jdbcTemplate.update(sql, transferTypeId, transferStatusID, transferId);
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Long transferId = results.getLong("transfer_id");
        Long transferTypeId = results.getLong("transfer_type_id");
        Long transferStatusId = results.getLong("transfer_status_id");
        Long accountFrom = results.getLong("account_from");
        Long accountTo = results.getLong("account_to");
       Double amount = results.getDouble("amount");

        Transfer transfer = new Transfer(transferId, transferTypeId, transferStatusId, accountFrom, accountTo, amount);
        return transfer;
    }

    private TransferDetails mapToRowTransferDetails(SqlRowSet results){
        TransferDetails transferDetail = new TransferDetails();
        transferDetail.setTransferId(results.getLong("transfer_id"));
        transferDetail.setUsernameFrom(results.getString("account_from"));
        transferDetail.setUsernameTo(results.getString("account_to"));
        transferDetail.setTransferType(results.getString("transfer_type"));
        transferDetail.setTransferStatus(results.getString("transfer_status"));
        transferDetail.setAmount(results.getDouble("amount"));
        return transferDetail;
    }
}
