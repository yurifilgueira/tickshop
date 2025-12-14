package com.tickshop.booking.exceptions;

public class BookingAlreadyCanceledException extends RuntimeException {
    public BookingAlreadyCanceledException(String message) {
        super(message);
    }
}
