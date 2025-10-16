package com.sebbe.cinema.dtos.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sebbe.cinema.enums.TechnicalEquipment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingDto(
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate date,
        @Positive Long cinemaHallId,
        Long filmId,
        @Size(min = 2, max = 100) String speaker,
        @NotNull List<TechnicalEquipment> technicalEquipment
) {
}
