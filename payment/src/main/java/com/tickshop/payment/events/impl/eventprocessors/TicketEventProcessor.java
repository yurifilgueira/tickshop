package com.tickshop.payment.events.impl.eventprocessors;

import com.tickshop.payment.events.DomainEvent;
import com.tickshop.payment.events.EventProcessor;
import com.tickshop.payment.events.ticket.TicketEvent;
import reactor.core.publisher.Mono;

public interface TicketEventProcessor <R extends DomainEvent> extends EventProcessor<TicketEvent, R> {

    @Override
    default Mono<R> process(TicketEvent event) {
        return switch (event) {
            case TicketEvent.TicketReserved ticketReserved -> this.handle(ticketReserved);
            case TicketEvent.TicketReservationFailed ticketReservationFailed -> this.handle(ticketReservationFailed);
            case TicketEvent.TicketSold ticketSold -> this.handle(ticketSold);
        };
    }

    Mono<R> handle(TicketEvent.TicketReserved ticketReserved);
    Mono<R> handle(TicketEvent.TicketReservationFailed ticketReservationFailed);
    Mono<R> handle(TicketEvent.TicketSold ticketSold);
}
