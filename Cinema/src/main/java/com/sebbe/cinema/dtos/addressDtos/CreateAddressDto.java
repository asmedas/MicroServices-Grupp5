package com.sebbe.cinema.dtos.addressDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressDto(
        @NotBlank @Size(min = 5, max = 100) String street,
        @NotBlank @Size(min = 5, max = 100) String city,

        @NotBlank(message = "Postal code must be a valid 5-digit number") @Size(min = 5, max = 5)
        @Pattern(regexp = "^[0-9]+$", message = "Postal code must be a valid 5-digit number") String postalCode
) {
}
