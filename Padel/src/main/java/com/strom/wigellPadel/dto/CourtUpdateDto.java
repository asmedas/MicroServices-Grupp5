package com.strom.wigellPadel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourtUpdateDto (
        @NotBlank @Size(max = 50) String information,
        @NotBlank double price,
        @NotBlank boolean available
){
}
