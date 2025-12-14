package com.tickshop.booking.model.dtos.response;

import java.util.UUID;

public record BookingCanceledResponse(
        UUID bookingId,
        String reason,
        String message
) {
}
