package com.sebbe.cinema.dtos.addressDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressDto(
        @NotBlank @Size(min = 5, max = 100) String street,
        @NotBlank @Size(min = 5, max = 100) String city,
        @NotBlank @Size(min = 5, max = 10) @Pattern(regexp = "^[0-9]+$")
        String postalCode
) {
}
