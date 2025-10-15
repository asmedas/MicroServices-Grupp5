package com.strom.wigellPadel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressCreateDto (
        @NotBlank @Size(max = 50) String street,
        @NotBlank @Size(max = 50) String postalCode,
        @NotBlank @Size(max = 50) String city
){
}
