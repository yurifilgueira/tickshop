package com.tickshop.tickets.models.entities;

import com.tickshop.tickets.models.enums.TicketStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Table("tickets")
public class Ticket implements Persistable<UUID>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID ticketId;
    @Column("show_id")
    private UUID showId;
    @Column("seat_number")
    private Long seatNumber;
    @Column("price")
    private BigDecimal price;
    @Column("status")
    private TicketStatus status;
    @Column("booking_id")
    private UUID bookingId;

    @Transient
    private boolean isNew = false;

    public Ticket() {}

    public Ticket(UUID showId, Long seatNumber, BigDecimal price) {
        this.ticketId = UUID.randomUUID();
        this.showId = showId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = TicketStatus.AVAILABLE;
        this.isNew = true;
    }

    public void reserve(UUID bookingId) {
        this.status = TicketStatus.RESERVED;
        this.bookingId = bookingId;
    }

    public void release() {
        this.status = TicketStatus.AVAILABLE;
        this.bookingId = null;
    }

    public void sell() {
        this.status = TicketStatus.SOLD;
    }

    public void confirm() {
        this.status = TicketStatus.SOLD;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getShowId() {
        return showId;
    }

    public void setShowId(UUID showId) {
        this.showId = showId;
    }

    public Long getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Long seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public UUID getId() {
        return ticketId;
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
