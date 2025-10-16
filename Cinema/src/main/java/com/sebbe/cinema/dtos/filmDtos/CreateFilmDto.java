package com.sebbe.cinema.dtos.filmDtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateFilmDto(
        @Positive @Max(18) int ageLimit,
        @NotBlank @Size(max = 250) String title,
        @NotBlank @Size(max = 100) String genre,
        @NotBlank @Positive @Max(300) int length
) {
}
