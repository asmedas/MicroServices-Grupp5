package com.strom.wigellPadel.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingUpdateDto (
        @NotNull LocalDate date,
        @NotNull int timeSlot,
        @NotNull int numberOfPlayers,
        @NotNull Long courtId
){
}
