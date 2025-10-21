package com.sebbe.cinema.dtos.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sebbe.cinema.enums.TechnicalEquipment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingDto(
        @NotNull(message = "Date is required and must be in yyyy-MM-dd format")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        @Positive(message = "Cinema hall ID must be a positive number")
        Long cinemaHallId,
        Long filmId,
        @Size(min = 2, max = 100, message = "Speaker name must be between 2 and 100 characters")
        String speaker,
        @NotNull(message = "Technical equipment list is required, see TechnicalEquipment enums")
        List<TechnicalEquipment> technicalEquipment
) {
}
