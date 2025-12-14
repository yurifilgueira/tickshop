package com.tickshop.booking.services;

import com.tickshop.booking.events.impl.events.BookingEvent;
import com.tickshop.booking.events.impl.publishers.BookingEventPublisher;
import com.tickshop.booking.events.tickets.TicketEvent;
import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.dtos.response.BookingCanceledResponse;
import com.tickshop.booking.model.dtos.response.CreateBookingResponse;
import com.tickshop.booking.model.enities.Booking;
import com.tickshop.booking.model.enums.BookingStatus;
import com.tickshop.booking.model.mappers.BookingMapper;
import com.tickshop.booking.repositories.BookingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.awt.print.Book;
import java.time.Instant;
import java.util.UUID;

@Service
public class BookingService {

    private BookingEventPublisher publisher;
    private BookingRepository bookingRepository;
    private final static String BOOKING_CANCELED_BY_CUSTOMER = "Booking canceled by customer";

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

    public Mono<Void> confirmBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.COMPLETED);
                    return bookingRepository.save(booking);
                }).then();
    }

    public Mono<Void> cancelBooking(TicketEvent.TicketReservationFailed event) {
        return bookingRepository.findById(event.bookingId())
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    return bookingRepository.save(booking);
                }).then();
    }

    public Mono<BookingCanceledResponse> cancelBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    return bookingRepository.save(booking).map(b ->
                            new BookingCanceledResponse(bookingId, BOOKING_CANCELED_BY_CUSTOMER, "Booking canceled successfully"));
                }).doOnSuccess(_ -> {
                    BookingEvent bookingCanceled = new BookingEvent.BookingCancelled(
                            bookingId,
                            Instant.now(),
                            BOOKING_CANCELED_BY_CUSTOMER
                    );
                    publisher.publish(bookingCanceled);
                });
    }

}
