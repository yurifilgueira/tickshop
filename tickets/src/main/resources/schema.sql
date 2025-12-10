CREATE TABLE IF NOT EXISTS tickets (
    ticket_id UUID PRIMARY KEY,
    show_id UUID NOT NULL,
    seat_number int NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    booking_id UUID,

    CONSTRAINT uq_show_seat UNIQUE (show_id, seat_number)
);