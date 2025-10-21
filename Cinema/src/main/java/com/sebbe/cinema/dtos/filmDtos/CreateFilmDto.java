package com.sebbe.cinema.dtos.filmDtos;

import jakarta.validation.constraints.*;

public record CreateFilmDto(
        @Positive @Max(18) Integer ageLimit,
        @NotBlank @Size(max = 250) String title,
        @NotBlank @Size(max = 100) String genre,
        @NotNull @Positive @Max(300) Integer length
) {
}
