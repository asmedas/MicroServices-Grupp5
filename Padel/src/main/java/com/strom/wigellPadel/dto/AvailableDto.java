package com.strom.wigellPadel.dto;

import java.time.LocalDate;

public record AvailableDto(
        Long courtId,
        LocalDate date,
        int timeSlot,
        double price,
        double priceInEUR
) {
}
