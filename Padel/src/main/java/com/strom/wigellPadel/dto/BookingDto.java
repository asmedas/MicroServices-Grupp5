package com.strom.wigellPadel.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingDto (
        Long id,
        Long customerId,
        Long courtId,
        int numberOfPlayers,
        LocalDate date,
        int timeSlot,
        double totalPrice,
        double priceInEUR
){
}
