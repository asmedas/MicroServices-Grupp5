package com.strom.wigellPadel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingCreateDto (
        @NotNull Long customerId,
        @NotNull Long courtId,
        @NotNull int numberOfPlayers,
        @NotNull LocalDate date,
        @NotNull int timeSlot
){
}
