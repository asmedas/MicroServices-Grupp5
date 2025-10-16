package com.sebbe.cinema.dtos.cinemahallDtos;

import com.sebbe.cinema.enums.TechnicalEquipment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCinemaHallDto(
        @NotBlank @Size(min = 1, max = 50) String name,
        @NotBlank @Positive @Max(5000) Integer maxSeats,
        List<TechnicalEquipment> technicalEquipment
) {
}
