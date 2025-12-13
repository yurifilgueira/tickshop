package com.tickshop.payment.model.mappers;

import com.tickshop.payment.model.dtos.PaymentDto;
import com.tickshop.payment.model.entities.Payment;

public class PaymentMapper {

    public static PaymentDto toPaymentDto(Payment payment){
        return new PaymentDto(payment.getId(), payment.getBookingId(), payment.getCustomerId(), payment.getAmount(), payment.getStatus(), payment.getCreatedAt());
    }

    public static Payment toPaymentEntity(PaymentDto paymentDto){
        Payment payment = new Payment();

        payment.setPaymentId(paymentDto.paymentId());
        payment.setBookingId(paymentDto.bookingId());
        payment.setCustomerId(paymentDto.customerId());
        payment.setAmount(paymentDto.amount());
        payment.setStatus(paymentDto.status());
        payment.setCreatedAt(paymentDto.createdAt());

        return payment;
    }

}
