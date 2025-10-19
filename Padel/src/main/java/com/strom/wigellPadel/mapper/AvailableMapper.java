package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.AvailableDto;
import com.strom.wigellPadel.dto.CourtDto;
import com.strom.wigellPadel.entities.Booking;
import com.strom.wigellPadel.entities.Court;

import java.time.LocalDate;

public class AvailableMapper {

    public AvailableMapper() {
    }

    public static AvailableDto toDto(Court court, LocalDate date, int timeSlot, double priceInEUR) {
        if (court == null) {
            return null;
        }

        return new AvailableDto(
                court.getId(),
                date,
                timeSlot,
                court.getPrice(),
                priceInEUR
        );
    }
}
