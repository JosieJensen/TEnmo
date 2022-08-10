package com.techelevator.tenmo.serverexceptions;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException() {super("This account was not found in our database. Please try again.");}
}
