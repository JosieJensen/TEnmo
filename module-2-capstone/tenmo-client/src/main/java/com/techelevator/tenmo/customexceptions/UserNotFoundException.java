package com.techelevator.tenmo.customexceptions;

public class UserNotFoundException extends java.lang.Exception {
    public UserNotFoundException() {super("This user was not found in our database. Please try again.");}
}
