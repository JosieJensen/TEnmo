package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TransferDao {

    void createTransfer( Transfer transfer, Long transferTypeId, Long transferStatusId, Long accountTo, Long accountFrom, Double amount);

    Transfer[] getAllTransfers();

   Transfer getTransferById(Long transferId);

    List<Transfer> getTransfersByUserId(Long userId);

    List<Transfer> getPendingTransfers(Long userId);

   void updateTransfer(Transfer transfer, Long transferStatusID, Long transferTypeId, Long transferId);

    Transfer[]  getTransfersByAccountId(Long accountId);

    TransferDetails[] getTransferDetails(Long id);

    void createTrans(Transfer transfer);
}
