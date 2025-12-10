package com.tickshop.booking.model.enities;

import com.tickshop.booking.model.enums.BookingStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table(name = "bookings")
public class Booking implements Persistable<UUID>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("booking_id")
    private UUID bookingId;
    @Column("customer_id")
    private UUID customerId;
    @Column("show_id")
    private UUID showId;
    @Column("quantity")
    private Integer quantity;
    @Column("amount")
    private BigDecimal amount;
    @Column("status")
    private BookingStatus status;
    @Column("created_at")
    private Instant createdAt;

    @Transient
    private boolean isNew = false;

    public Booking() {}

    public Booking(UUID customerId, UUID showId, Integer quantity, BigDecimal amount) {
        this.bookingId = UUID.randomUUID();
        this.customerId = customerId;
        this.showId = showId;
        this.quantity = quantity;
        this.amount = amount;
        this.status = BookingStatus.PENDING;
        this.createdAt = Instant.now();
        this.isNew = true;
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

    public UUID getShowId() {
        return showId;
    }

    public void setShowId(UUID showId) {
        this.showId = showId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public UUID getId() {
        return bookingId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Transient
    public void markAsExisting() {
        this.isNew = false;
    }


}
