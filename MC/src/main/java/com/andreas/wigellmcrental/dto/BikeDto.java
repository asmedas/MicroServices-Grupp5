package com.andreas.wigellmcrental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BikeDto(
        Long id,
        @NotBlank String brand,
        @NotBlank String model,
        @NotNull @Min(1900) int year,
        @Min(100) double pricePerDay,
        boolean available
) {}
