package com.tickshop.tickets.models.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTicketRequest(

        @NotNull(message = "O ID do show é obrigatório")
        UUID showId,

        @NotBlank(message = "O número do assento é obrigatório")
        Long seatNumber,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal price
) {
}
