package com.tickshop.tickets.events.impl.eventprocessors;

import com.tickshop.tickets.events.DomainEvent;
import com.tickshop.tickets.events.EventProcessor;
import com.tickshop.tickets.events.impl.events.TicketEvent;
import reactor.core.publisher.Mono;

public interface TicketEventProcessor<R extends DomainEvent> extends EventProcessor<TicketEvent, R> {

    @Override
    default Mono<R> process(TicketEvent event) {
        return switch (event) {
            case TicketEvent.TicketSold ticketSold -> this.handle(ticketSold);
            case TicketEvent.TicketReservationFailed ticketReservationFailed -> this.handle(ticketReservationFailed);
            case TicketEvent.TicketReserved ticketReserved -> this.handle(ticketReserved);
        };
    }

    Mono<R> handle(TicketEvent.TicketReserved ticketReserved);
    Mono<R> handle(TicketEvent.TicketReservationFailed ticketReservationFailed);
    Mono<R> handle(TicketEvent.TicketSold ticketSold);

}
