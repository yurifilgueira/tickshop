package com.tickshop.booking.model.dtos.response;

import com.tickshop.booking.model.enums.BookingStatus;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record CreateBookingResponse(
        @NonNull
        UUID bookingId,
        @NonNull
        BookingStatus status
) {
}
