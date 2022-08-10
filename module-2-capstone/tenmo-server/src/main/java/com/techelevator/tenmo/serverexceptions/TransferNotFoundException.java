package com.techelevator.tenmo.serverexceptions;

public class TransferNotFoundException extends Exception {
    public TransferNotFoundException() {super("This transfer was not found in our database. Please try again.");}
}
