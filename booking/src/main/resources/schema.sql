CREATE TABLE IF NOT EXISTS bookings (
    booking_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    show_id UUID NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);