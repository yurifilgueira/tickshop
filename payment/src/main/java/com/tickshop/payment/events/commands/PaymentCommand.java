package com.tickshop.payment.events.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "commandType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentCommand.ProcessPaymentCommand.class, name = "ProcessPaymentCommand"),
        @JsonSubTypes.Type(value = PaymentCommand.RefundPaymentCommand.class, name = "RefundPaymentCommand")
})
public sealed interface PaymentCommand permits PaymentCommand.ProcessPaymentCommand, PaymentCommand.RefundPaymentCommand {

    record ProcessPaymentCommand(
            UUID bookingId,
            UUID customerId,
            BigDecimal amount
    ) implements PaymentCommand {}

    record RefundPaymentCommand(
            UUID bookingId
    ) implements PaymentCommand {}
}