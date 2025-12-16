package com.tickshop.booking.services;

import com.tickshop.booking.events.commands.PaymentCommand;
import com.tickshop.booking.events.commands.TicketCommand;
import com.tickshop.booking.events.publishers.PaymentCommandPublisher;
import com.tickshop.booking.events.publishers.TicketCommandPublisher;
import com.tickshop.booking.events.tickets.TicketEvent;
import com.tickshop.booking.exceptions.BookingAlreadyCanceledException;
import com.tickshop.booking.exceptions.BookingNotFoundException;
import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.dtos.response.BookingCanceledResponse;
import com.tickshop.booking.model.dtos.response.CreateBookingResponse;
import com.tickshop.booking.model.enities.Booking;
import com.tickshop.booking.model.enums.BookingStatus;
import com.tickshop.booking.model.mappers.TicketCommandMapper;
import com.tickshop.booking.repositories.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class BookingService {

    private final TicketCommandPublisher ticketPublisher;
    private final PaymentCommandPublisher paymentCommandPublisher;
    private final BookingRepository bookingRepository;
    private final static String BOOKING_CANCELED_BY_CUSTOMER = "Booking canceled by customer";
    private final static Logger log = LoggerFactory.getLogger(BookingService.class);

    public BookingService(TicketCommandPublisher ticketPublisher, PaymentCommandPublisher paymentCommandPublisher, BookingRepository bookingRepository) {
        this.ticketPublisher = ticketPublisher;
        this.paymentCommandPublisher = paymentCommandPublisher;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Mono<CreateBookingResponse> save(CreateBookingRequest createBookingRequest) {
        Booking booking = TicketCommandMapper.dtoToEntity(createBookingRequest);
        return bookingRepository.save(booking).map(b -> {
            return new CreateBookingResponse(b.getBookingId(), b.getStatus());
        }).doOnSuccess(_ -> {
            var event = TicketCommandMapper.toReserveTicketCommand(booking);
            ticketPublisher.publish(event);
        });
    }

    @Transactional
    public Mono<Void> confirmBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.COMPLETED);
                    return bookingRepository.save(booking);
                }).then();
    }

    @Transactional
    public Mono<Void> cancelBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.CANCELING);
                    return bookingRepository.save(booking);
                }).doOnSuccess(_ -> {
                    log.debug("Canceling booking: {}", bookingId);
                    PaymentCommand paymentCommand = new PaymentCommand.RefundPaymentCommand(
                            bookingId
                    );
                    paymentCommandPublisher.publish(paymentCommand);
                }).then();
    }

    @Transactional
    public Mono<BookingCanceledResponse> cancelBookingAndPublishCancelReservation(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new BookingNotFoundException("Booking not found")))
                .flatMap(booking -> {

                    if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
                        return Mono.error(new BookingAlreadyCanceledException("Booking is already canceled"));
                    }

                    booking.setStatus(BookingStatus.CANCELLED);
                    return bookingRepository.save(booking).map(b ->
                            new BookingCanceledResponse(bookingId, BOOKING_CANCELED_BY_CUSTOMER, "Booking canceled successfully"));
                }).doOnSuccess(_ -> {
                    log.debug("Booking {} canceled successfully", bookingId);
                    TicketCommand bookingCanceled = new TicketCommand.CancelReservationCommand(
                            bookingId,
                            BOOKING_CANCELED_BY_CUSTOMER
                    );
                    ticketPublisher.publish(bookingCanceled);
                });
    }

    public Mono<Void> processPayment(TicketEvent.TicketReserved ticketReserved) {
        return Mono.fromRunnable(() ->
                paymentCommandPublisher.publish(new PaymentCommand.ProcessPaymentCommand(
                        ticketReserved.bookingId(),
                        ticketReserved.customerId(),
                        ticketReserved.amount()
                ))
        );
    }

    public Mono<Void> sellTicket(UUID bookingId) {
        return Mono.fromRunnable(() -> {
            ticketPublisher.publish(new TicketCommand.SellTicketCommand(bookingId));
        });
    }

}