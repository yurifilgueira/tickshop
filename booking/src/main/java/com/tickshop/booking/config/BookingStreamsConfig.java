package com.tickshop.booking.config;

import com.tickshop.booking.events.commands.PaymentCommand;
import com.tickshop.booking.events.commands.TicketCommand;
import com.tickshop.booking.events.publishers.PaymentCommandPublisher;
import com.tickshop.booking.events.publishers.TicketCommandPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Configuration
public class BookingStreamsConfig {

    @Bean
    public Supplier<Flux<TicketCommand>> ticketProducer(TicketCommandPublisher publisher) {
        return publisher::getGeneralFlux;
    }

    @Bean
    public Supplier<Flux<PaymentCommand>> paymentProducer(PaymentCommandPublisher publisher) {
        return publisher::getGeneralFlux;
    }

}