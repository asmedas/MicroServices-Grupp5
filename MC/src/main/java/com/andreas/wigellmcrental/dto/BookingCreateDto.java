package com.andreas.wigellmcrental.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookingCreateDto(
        @NotNull Long customerId,
        @NotNull Long bikeId,
        @NotNull @Future LocalDate startDate,
        @NotNull @Future LocalDate endDate
) {}

