package com.techelevator.tenmo.services;

import com.techelevator.tenmo.customexceptions.InvalidTransferIdException;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;

public interface TransferService {

    public Transfer createTransfer(Transfer transfer);
    public Transfer[] getAllTransfers();
    public Transfer getTransferById(AuthenticatedUser authenticatedUser, Long transferId);
    Transfer[] getTransfersByAccountId(Long accountId);

}
