package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.util.*;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private AccountServiceREST accountServiceREST;
    private TransferServiceREST transferServiceREST;
    private UserService userService;



    public App() {
    }


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        accountServiceREST = new AccountServiceREST(API_BASE_URL, currentUser);
        Double balance = accountServiceREST.getBalance();
        System.out.println("Your current account balance is: " + consoleService.currencyFormat(balance));
	}

	private void viewTransferHistory() {
//        instantiating services needed
        transferServiceREST = new TransferServiceREST(API_BASE_URL, currentUser);
        userService = new UserService(API_BASE_URL, currentUser);

//        establishing user & getting ID in order to print
        Transfer[] userTransfers = transferServiceREST.getTransfersByUserId(currentUser.getUser().getId());
        System.out.println("------------------------------------------------------------------\n" +
                "Transfer ID     " + "Sender Account      " + "Recipient Account       "+ "Amount             \n" +
                "------------------------------------------------------------------\n");
        Set<String> transferSet = new TreeSet<>();
        for (Transfer transferList : userTransfers) {
            if (!transferList.getTransferId().equals(null)) {
                transferSet.add(transferList.getTransferId() + "            " + transferList.getAccountFrom() + "                " + transferList.getAccountTo() + "                    " + consoleService.currencyFormat(transferList.getAmount()));
            }
        }
        for (String transSet :transferSet) {
            System.out.println(transSet);
        }
//        getting the transfer ID to access Transfer Details
        System.out.println("------------------------------------------------------------------\n");
        Long transferToViewID = consoleService.promptForLong("If you'd like to view the details of a particular transfer, please enter the Transfer ID here: ");
//      Check if user choice of transfer ID is valid
        boolean exists;
        exists =  doesTransferExist(transferToViewID);
        if (!exists) {
            System.out.println("The Transfer ID does not exist. Please try again. \n" );
        } else {
            TransferDetails[] transferDetails = transferServiceREST.getAllTransferDetails(currentUser.getUser().getId());
            for (TransferDetails transferDetailsList : transferDetails) {
                if (transferToViewID.equals(transferDetailsList.getTransferId())) {
                    System.out.println("------------------------------------------------------------------\n" +
                            "Transfer ID" + "      " + "Status" + "      " + "Type" + "      " + "To" + "      " + "From" + "       " + "Amount " + "\n" +
                            "------------------------------------------------------------------\n" + "\n");
                    System.out.println(transferDetailsList.toString());
                }
            }
        }

    }

	private void viewPendingRequests() {
        transferServiceREST = new TransferServiceREST(API_BASE_URL, currentUser);
        accountServiceREST = new AccountServiceREST(API_BASE_URL, currentUser);
        userService = new UserService(API_BASE_URL, currentUser);

        TransferDetails[] transferDetails = transferServiceREST.getAllTransferDetails(currentUser.getUser().getId());

//        Gathering sender Account info
        Account senderAccount = accountServiceREST.getAccountByUserId(currentUser.getUser().getId());
        Long senderAccountID = senderAccount.getAccountId();
        Double senderBalance = senderAccount.getBalance();
        String senderUsername = currentUser.getUser().getUsername();

        System.out.println("------------------------------------------------------------------\n" +
                "Transfer ID" + "      " + "Status" + "      " + "Type" + "      " + "To" + "      " + "From" + "       " + "Amount " + "\n" +
                "------------------------------------------------------------------\n" + "\n");
//            Print Pending Transfer Details
        for (TransferDetails transferDetailsList : transferDetails) {
            if (transferDetailsList.getTransferStatus().equalsIgnoreCase("Pending") && transferDetailsList.getUsernameFrom().equalsIgnoreCase(senderUsername)) {
                System.out.println(transferDetailsList.toString());
            }
        }

        Long transferToHandleID = consoleService.promptForLong("Please enter the transfer ID of the transfer you'd like to handle: ");
//        Gather Transfer Info
        boolean exists;
        exists = doesTransferExist(transferToHandleID);
        if (!exists) {
            System.out.println("The Transfer ID does not exist. Please try again. \n");
        } else {
            Transfer transferToHandle = transferServiceREST.getTransferById(currentUser, transferToHandleID);
            Long sendToID = transferToHandle.getAccountTo();
            Double amount = transferToHandle.getAmount();
//      Print Pending Request options
            consoleService.printRequestHandleOptions();
            Long userChoice = consoleService.promptForLong("Please select the number of the option you'd like to choose: ");

//          Handle user choices & update per user choice
            if (userChoice == 1 && amount > senderBalance) {
                System.out.println("This transaction cannot be approved. ");
            }
            if (userChoice == 1 && amount <= senderBalance) {
                String recipientUsername = consoleService.promptForString("You have chosen to approve the request for Transfer " + transferToHandleID + "\n" +
                        "Please confirm by entering the Username of the requester. (Press 0 to cancel) ");

                Long recipientUserID = userService.getByUserByUsername(recipientUsername).getId();
                Account recipientAccount = accountServiceREST.getAccountByUserId(recipientUserID);
                Long recipientAccountAccountId = recipientAccount.getAccountId();

                Account senderAccount1 = accountServiceREST.getAccountByUserId(currentUser.getUser().getId());
                Long senderAccountID1 = senderAccount.getAccountId();

                transferToHandle.setTransferStatusId(2L);
                transferToHandle.setTransferTypeId(2L);
                transferServiceREST.updateTransfer(transferToHandle, transferToHandleID);
                System.out.println("Transfer " + transferToHandleID + "is now approved and will be initiated. \n");

                accountServiceREST.withdrawAccount(senderAccount1, currentUser.getUser().getId(), amount);
                accountServiceREST.depositAccount(recipientAccount, recipientUserID, amount);

                Double updatedBalance = accountServiceREST.getAccountByAccountId(currentUser, senderAccountID1).getBalance();
                System.out.println("Your new account balance is " + consoleService.currencyFormat(updatedBalance) + ". \n");

            } if (userChoice == 2) {
                transferToHandle.setTransferStatusId(3L);
                transferToHandle.setTransferTypeId(1L);
                transferServiceREST.updateTransfer(transferToHandle, transferToHandleID);

                System.out.println("You have chosen to reject Transfer " + transferToHandleID + ". Press Enter to return to the main menu.");
            } if (userChoice == 0) {
                System.out.println("You have chosen to neither reject or approve Transfer " + transferToHandleID + ". Press Enter to return to the main menu.");
            }
        }
    }

	private void sendBucks() {
        accountServiceREST = new AccountServiceREST(API_BASE_URL, currentUser);
        userService = new UserService(API_BASE_URL, currentUser);
        transferServiceREST = new TransferServiceREST(API_BASE_URL, currentUser);
//        Showing the user their current balance so they can decide how much money to transfer
        System.out.println("-------------------------------------------\n" +
                "Your current account balance is : " + consoleService.currencyFormat(accountServiceREST.getBalance()));
//        Presenting a list of users to choose from
        User[] users = userService.getAllUsers(currentUser);
        System.out.println("-------------------------------------------\n" +
                "User ID             " + "Name \n" +
                "-------------------------------------------\n");
        for (User userlist : users) {
            if (userlist.getId() != currentUser.getUser().getId()) {
                System.out.println(userlist.getId() + "                " + userlist.getUsername());

                System.out.println("-------------------------------------------\n");
            }
        }
//        Getting the user & amount to send to
        Long sendToUserID = consoleService.promptForLong("Please select the user ID that you'd like to send TEnmo bucks to (0 to cancel): ");
//            Check's user choice
        if (consoleService.userValidation(sendToUserID, users, currentUser)) {
            System.out.println("You have selected User ID: " + sendToUserID + "\n");
//
//        Gather info to initiate transfer
            Double amountToSend = consoleService.promptForDouble("Please indicate the amount of TEBucks you'd like to send to User ID " + sendToUserID + ": ");

            Long sendFromUserID = currentUser.getUser().getId();
            Account senderAccount = accountServiceREST.getAccountByUserId(sendFromUserID);
            Long sendFrom = senderAccount.getAccountId();

            Account recieverAccount = accountServiceREST.getAccountByUserId(sendToUserID);
            Long sendTo = recieverAccount.getAccountId();

            Double currentBalance = accountServiceREST.getBalance();

//      Create transfer & and implement withdrawal/deposit
            if (amountToSend > currentBalance) {
                System.out.println("You cannot send more that your account balance of: " + consoleService.currencyFormat(currentBalance));
            }
            else if (amountToSend > 0 && amountToSend <= accountServiceREST.getBalance()) {
                Transfer transfer = new Transfer();
                transfer.setTransferTypeId(2L);
                transfer.setTransferStatusId(2L);
                transfer.setAccountFrom(sendFrom);
                transfer.setAccountTo(sendTo);
                transfer.setAmount(amountToSend);

                transferServiceREST.createTransfer(transfer);

                accountServiceREST.withdrawAccount(senderAccount, sendFromUserID, amountToSend);

                accountServiceREST.depositAccount(recieverAccount, sendToUserID, amountToSend);
                Double updatedBalance = accountServiceREST.getBalance();

                System.out.println("------------------------------------------------------\n");
                System.out.println("Your transfer to User ID: " + sendToUserID + " for the amount of " + consoleService.currencyFormat(amountToSend) + " has been initiated. \n" +
                        "Your new account balance is: " + consoleService.currencyFormat(accountServiceREST.getBalance()) + "\n");
            }
//          Get user's updated balance to print
//            Double updatedBalance = accountServiceREST.getBalance();
//
//            System.out.println("------------------------------------------------------\n");
//            System.out.println("Your transfer to User ID: " + sendToUserID + " for the amount of " + consoleService.currencyFormat(amountToSend) + " has been initiated. \n" +
//                    "Your new account balance is: " + consoleService.currencyFormat(accountServiceREST.getBalance()) + "\n");
        }
    }


	private void requestBucks() {
        accountServiceREST = new AccountServiceREST(API_BASE_URL, currentUser);
        userService = new UserService(API_BASE_URL, currentUser);
        transferServiceREST = new TransferServiceREST(API_BASE_URL, currentUser);

//      Get & print users
        User[] users = userService.getAllUsers(currentUser);
        consoleService.printUsers(users);
//      Get user selection
        Long senderUserId = consoleService.promptForLong("Enter the ID of the user you'd like to request money from.(0 to cancel) ");
        Long requesterUserId  = currentUser.getUser().getId();
        System.out.println("You have selected User ID: " + senderUserId + "\n");
//
//        Gather info to initiate request
        Double amountToRequest = consoleService.promptForDouble("Please indicate the amount of money you'd like to request from User ID " + senderUserId + ": ");
//        Gather account info
        Account senderAccount = accountServiceREST.getAccountByUserId(senderUserId);
        Account requesterAccount = accountServiceREST.getAccountByUserId(requesterUserId);
        Long accountFrom = senderAccount.getAccountId();
        Long accountTo = requesterAccount.getAccountId();
//        create pending transfer request
        if (amountToRequest > 0) {
            Transfer transfer = new Transfer();
            transfer.setTransferTypeId(1L);
            transfer.setTransferStatusId(1L);
            transfer.setAccountFrom(accountFrom);
            transfer.setAccountTo(accountTo);
            transfer.setAmount(amountToRequest);

            transferServiceREST.createTransfer(transfer);
        }

        System.out.println("------------------------------------------------------\n");
        System.out.println("Your request to User ID: " + senderUserId + " for the amount of " + consoleService.currencyFormat(amountToRequest) + " is now pending. \n");

    }

    private boolean doesTransferExist(Long id){
        boolean exists = false;
        transferServiceREST = new TransferServiceREST(API_BASE_URL, currentUser);
        Transfer[] transfers = transferServiceREST.getAllTransfers();
        for(Transfer t: transfers){
            if(t.getTransferId().equals(id)){
                exists = true;
            }
        }
        return exists;
    }


}
