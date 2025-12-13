package com.tickshop.payment.model.entities;

import com.tickshop.payment.model.enums.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("payments")
public class Payment implements Persistable<UUID> {

    @Id
    @Column("payment_id")
    private UUID paymentId;

    @Column("booking_id")
    private UUID bookingId;
    @Column("customer_id")
    private UUID customerId;
    @Column("amount")
    private BigDecimal amount;
    @Column("status")
    private PaymentStatus status;
    @Column("created_at")
    private Instant createdAt;

    @Transient
    private boolean isNew = false;

    public Payment() {}

    public Payment(UUID bookingId, UUID customerId, BigDecimal amount) {
        this.paymentId = UUID.randomUUID();
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
        this.isNew = true;
    }

    public void approve() {
        this.status = PaymentStatus.APPROVED;
    }

    public void decline() {
        this.status = PaymentStatus.DECLINED;
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }

    @Override
    public UUID getId() {
        return paymentId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Transient
    public void markAsExisting() {
        this.isNew = false;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }
}