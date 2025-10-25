package com.miriam.travel.dto.destination;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DestinationCreateUpdateRequest {
    @NotBlank public String hotelName;
    @NotBlank public String city;
    @NotBlank public String country;
    @NotNull  public BigDecimal pricePerWeekSek;
}
