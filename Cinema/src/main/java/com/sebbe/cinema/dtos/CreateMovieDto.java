package com.sebbe.cinema.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record CreateMovieDto(
        @Positive @Max(18) int ageLimit,
        @NotBlank @Size(max = 250) String title,
        @NotBlank @Size(max = 100) String genre
) {
}
