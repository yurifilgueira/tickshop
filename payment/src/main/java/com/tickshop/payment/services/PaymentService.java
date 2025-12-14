package com.tickshop.payment.services;

import com.tickshop.payment.events.ticket.TicketEvent;
import com.tickshop.payment.model.entities.Payment;
import com.tickshop.payment.model.enums.PaymentStatus;
import com.tickshop.payment.repositories.CustomerRepository;
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
    private final CustomerRepository customerRepository;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Mono<Payment> processPayment(TicketEvent.TicketReserved ticketReserved) {
        return customerRepository.findById(ticketReserved.customerId())
                .flatMap(c -> {
                    BigDecimal amount = ticketReserved.amount();
                    if(c.getBalance().compareTo(amount) < 0) {

                        Payment payment = new Payment(ticketReserved.bookingId(), ticketReserved.customerId(), amount);
                        payment.setStatus(PaymentStatus.DECLINED);

                        return paymentRepository.save(payment);
                    }
                    c.setBalance(c.getBalance().subtract(amount));
                    return customerRepository.save(c)
                            .flatMap(_ -> {
                                Payment payment = new Payment(
                                        ticketReserved.bookingId(),
                                        ticketReserved.customerId(),
                                        amount
                                );
                                payment.setStatus(PaymentStatus.APPROVED);
                                return paymentRepository.save(payment);
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
    }

    @Transactional
    public Mono<Payment> refundPayment(UUID paymentId) {
        return paymentRepository.findByBookingId(paymentId)
                .flatMap(payment -> {
                    return customerRepository.findById(payment.getCustomerId())
                            .flatMap(customer -> {
                                customer.setBalance(customer.getBalance().add(payment.getAmount()));
                                return customerRepository.save(customer);
                            })
                            .flatMap(_ -> {
                                payment.setStatus(PaymentStatus.REFUNDED);
                                return paymentRepository.save(payment);
                            });
                });
    }
}
