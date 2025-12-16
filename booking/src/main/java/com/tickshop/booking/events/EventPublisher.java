package com.tickshop.booking.events;

import com.tickshop.booking.events.commands.TicketCommand;

public interface EventPublisher <T extends DomainEvent> {
    void publish(TicketCommand event);
}
