package com.tickshop.booking.repositories;

import com.tickshop.booking.model.enities.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, UUID> {
}
