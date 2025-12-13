CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY,
    booking_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);