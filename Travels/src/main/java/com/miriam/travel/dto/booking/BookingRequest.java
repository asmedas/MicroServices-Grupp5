package com.miriam.travel.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequest {
    @NotBlank public String customerId;
    @NotNull  public Long destinationId;
    @NotNull  public LocalDate departureDate;
    @Min(1)   public int weeks;
}

