package com.sebbe.cinema.dtos.customerDtos;

import jakarta.validation.constraints.*;

public record CustomerUpdateDto(
        @NotBlank @Size(min = 1, max = 50) String firstName,
        @NotBlank @Size(min = 1, max = 50) String lastName,
        @NotBlank @Email String email,
        @NotNull @Positive @Max(120) Integer age
) {
}
