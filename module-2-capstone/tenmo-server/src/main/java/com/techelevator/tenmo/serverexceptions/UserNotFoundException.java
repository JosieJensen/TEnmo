package com.techelevator.tenmo.serverexceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {super("This user was not found in our database. Please try again.");}
}
