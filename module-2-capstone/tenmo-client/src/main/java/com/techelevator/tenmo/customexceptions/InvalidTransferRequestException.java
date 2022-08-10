package com.techelevator.tenmo.customexceptions;

public class InvalidTransferRequestException extends Exception {
    public InvalidTransferRequestException() { super("Invalid user ID. Please try again.");}
}
