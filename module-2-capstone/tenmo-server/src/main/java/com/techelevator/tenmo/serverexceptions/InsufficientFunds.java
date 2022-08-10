package com.techelevator.tenmo.serverexceptions;

public class InsufficientFunds extends Exception {
    public InsufficientFunds() {
        super("We know capitalism is hard. You don't have any money. :( ");
    }
}
