package com.tickshop.booking.controllers;

import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.enities.Booking;
import com.tickshop.booking.services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bookings")
public class BookingController {

    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest bookingRequest) {

        var response = bookingService.save(bookingRequest);

        return ResponseEntity.accepted().body(response);
    }
}
