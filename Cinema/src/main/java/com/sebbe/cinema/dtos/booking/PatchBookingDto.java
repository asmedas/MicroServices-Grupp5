package com.sebbe.cinema.dtos.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sebbe.cinema.enums.TechnicalEquipment;

import java.time.LocalDate;
import java.util.List;

public record PatchBookingDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate date,
        List<TechnicalEquipment> technicalEquipment
) {
}
