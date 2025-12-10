package com.tickshop.booking.services;

import com.tickshop.booking.events.impl.publishers.BookingEventPublisher;
import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.dtos.response.CreateBookingResponse;
import com.tickshop.booking.model.enities.Booking;
import com.tickshop.booking.model.mappers.BookingMapper;
import com.tickshop.booking.repositories.BookingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.awt.print.Book;

@Service
public class BookingService {

    private BookingEventPublisher publisher;
    private BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository, BookingEventPublisher publisher) {
        this.bookingRepository = bookingRepository;
        this.publisher = publisher;
    }

    public Mono<CreateBookingResponse> save(CreateBookingRequest createBookingRequest) {
        Booking booking = BookingMapper.dtoToEntity(createBookingRequest);
        return bookingRepository.save(booking).map(b -> {
            return new CreateBookingResponse(b.getBookingId(), b.getStatus());
        }).doOnSuccess(savedBooking -> {
            var event = BookingMapper.toBookingCreatedEvent(booking);
            publisher.publish(event);
        });
    }
}
