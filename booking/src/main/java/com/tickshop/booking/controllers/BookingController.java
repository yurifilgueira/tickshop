package com.tickshop.booking.controllers;

import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.enities.Booking;
import com.tickshop.booking.services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelBooking(@RequestParam("id") UUID bookingId) {
        return ResponseEntity.accepted().body(bookingService.cancelBooking(bookingId));
    }
}
