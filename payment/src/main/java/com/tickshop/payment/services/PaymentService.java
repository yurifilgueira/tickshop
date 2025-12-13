package com.tickshop.payment.services;

import com.tickshop.payment.events.ticket.TicketEvent;
import com.tickshop.payment.model.entities.Payment;
import com.tickshop.payment.model.enums.PaymentStatus;
import com.tickshop.payment.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Mono<Payment> processPayment(TicketEvent.TicketReserved ticketReserved) {

        Payment payment = new Payment(
                ticketReserved.bookingId(),
                UUID.fromString("6d28fb5e-f7b8-426e-b9b6-a29cd9fd9c6d"),
                new BigDecimal("100.50")
                );

        payment.setStatus(PaymentStatus.APPROVED);
        return paymentRepository.save(payment);

    }
}
