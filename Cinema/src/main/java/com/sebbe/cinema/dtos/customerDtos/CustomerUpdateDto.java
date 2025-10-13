package com.sebbe.cinema.dtos.customerDtos;

import jakarta.validation.constraints.*;

public record CustomerUpdateDto(
        @NotBlank @Size(min = 1, max = 50) String firstName,
        @NotBlank @Size(min = 1, max = 50) String lastName,
        @NotBlank @Email @Size(max = 50) String email,
        @NotBlank @Positive @Max(120) int age
) {
}
