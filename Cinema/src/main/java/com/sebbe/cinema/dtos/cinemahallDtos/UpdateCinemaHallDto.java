package com.sebbe.cinema.dtos.cinemahallDtos;

import com.sebbe.cinema.enums.TechnicalEquipment;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCinemaHallDto(
        @NotBlank @Size(min = 1, max = 50) String name,
        @NotNull @Positive @Max(5000) Integer maxSeats
) {
}
