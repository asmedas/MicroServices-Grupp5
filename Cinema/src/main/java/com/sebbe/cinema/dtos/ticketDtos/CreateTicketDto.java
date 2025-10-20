package com.sebbe.cinema.dtos.ticketDtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTicketDto(
        @NotNull @Positive Long screeningId
) {
}
