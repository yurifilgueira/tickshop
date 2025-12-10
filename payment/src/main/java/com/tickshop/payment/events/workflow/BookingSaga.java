package com.tickshop.payment.events.workflow;

import java.util.UUID;

public interface BookingSaga extends Saga{
    UUID orderId();
}
