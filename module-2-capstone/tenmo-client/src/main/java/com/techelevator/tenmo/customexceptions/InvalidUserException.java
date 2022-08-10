package com.techelevator.tenmo.customexceptions;

public class InvalidUserException extends Exception {
    public InvalidUserException() {

        super("Invalid user ID. Please try again.");
    }
}
