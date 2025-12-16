package com.tickshop.tickets.events.impl.processors;

import com.tickshop.tickets.events.DomainEvent;
import com.tickshop.tickets.events.EventProcessor;
import com.tickshop.tickets.events.booking.events.TicketCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public interface CommandProcessor<R> extends EventProcessor<TicketCommand, R> {

    Logger log = LoggerFactory.getLogger(CommandProcessor.class);

    default Mono<R> process(TicketCommand event) {
        return switch (event) {
            case TicketCommand.ReserveTicketCommand reserveTicketCommand -> this.handle(reserveTicketCommand);
            case TicketCommand.SellTicketCommand sellTicketCommand -> this.handle(sellTicketCommand);
            case TicketCommand.CancelReservationCommand cancelReservationCommand -> this.handle(cancelReservationCommand);
        };
    }

    Mono<R> handle(TicketCommand.ReserveTicketCommand reserveTicketCommand);
    Mono<R> handle(TicketCommand.SellTicketCommand sellTicketCommand);
    Mono<R> handle(TicketCommand.CancelReservationCommand cancelReservationCommand);

}
