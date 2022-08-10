package com.techelevator.tenmo.services;


import com.techelevator.tenmo.customexceptions.InvalidTransferIdException;
import com.techelevator.tenmo.customexceptions.InvalidUserException;
import com.techelevator.tenmo.customexceptions.UserNotFoundException;
import com.techelevator.tenmo.model.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.Set;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public Long promptForLong(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public Double promptForDouble(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public void printUsers(User[] users) {
        System.out.println("-------------------------------------------\n" +
                "User ID             " + "Name \n" +
                "-------------------------------------------\n");
        for (User userlist : users) {
                System.out.println(userlist.getId() + "                " + userlist.getUsername());

                System.out.println("-------------------------------------------\n");
        }
    }

    public String currencyFormat(Double money) {
        NumberFormat nF
                = NumberFormat.getCurrencyInstance();
        return nF.format(money);
    }


    public boolean userValidation(Long idChosen, User[] userList, AuthenticatedUser currentUser) {
        if (idChosen > 0) {
            try {
                boolean validChoice = false;

                for (User user : userList) {
                    if (idChosen.equals(currentUser.getUser().getId())) {
                        throw new InvalidUserException();
                    }
                    if (idChosen.equals(user.getId())) {
                        validChoice = true;
                        break;
                    }
                }
                if (validChoice == false) {
                    throw new UserNotFoundException();
                }
                return true;
            } catch (InvalidUserException | UserNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    public boolean transferValidation(Long idChosen, Transfer transfer, AuthenticatedUser currentUser) {
        if (idChosen > 0) {
            try {
                boolean validChoice = false;

                    if (idChosen.equals(transfer.getTransferId())) {
                        validChoice = true;
                    }
                    else  {
                       throw new InvalidTransferIdException();
                    }
                    return true;
            } catch (InvalidTransferIdException e) {
                    System.out.println(e.getMessage());
        }
    }
            return false;
        }

    public void printRequestHandleOptions() {
        System.out.println(
       "1: Approve \n" +
       "2: Reject \n" +
       "0: Don't approve or reject \n");

    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
