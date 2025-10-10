package com.sebbe.cinema.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerDto(
        @NotBlank @Size(min = 1, max = 100) String name
) {
}
