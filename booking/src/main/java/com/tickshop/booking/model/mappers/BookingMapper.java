package com.tickshop.booking.model.mappers;

import com.tickshop.booking.events.impl.events.BookingEvent;
import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.enities.Booking;

import java.time.Instant;

public class BookingMapper {

    public static BookingEvent.BookingCreated toBookingCreatedEvent(Booking entity) {
        return new BookingEvent.BookingCreated(
                entity.getBookingId(),
                entity.getCustomerId(),
                entity.getShowId(),
                entity.getQuantity(),
                entity.getAmount(),
                Instant.now()
        );
    }

    public static Booking dtoToEntity(CreateBookingRequest createBookingRequest) {
        return new Booking(
                createBookingRequest.customerId(),
                createBookingRequest.showId(),
                createBookingRequest.quantity(),
                createBookingRequest.amount()
        );
    }

}
