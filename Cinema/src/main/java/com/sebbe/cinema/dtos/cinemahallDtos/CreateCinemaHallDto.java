package com.sebbe.cinema.dtos.cinemahallDtos;

import jakarta.validation.constraints.*;


public record CreateCinemaHallDto(
        @NotBlank @Size(min = 1, max = 50) String name,
        @NotNull @Positive @Max(5000) Integer maxSeats
) {
}
