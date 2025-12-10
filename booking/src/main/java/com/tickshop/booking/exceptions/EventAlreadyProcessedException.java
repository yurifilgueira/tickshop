package com.tickshop.booking.exceptions;

public class EventAlreadyProcessedException extends RuntimeException {

    private static final String MESSAGE = "Event already processed";

    public EventAlreadyProcessedException(String message) {
        super(MESSAGE);
    }
}
