package com.sebbe.cinema.dtos.customerDtos;

import com.sebbe.cinema.dtos.addressDtos.CreateAddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateCustomerWithAccountDto(
        @NotBlank @Size(min = 1, max = 100) String firstName,
        @NotBlank @Size(min = 1, max = 100) String lastName,
        @Positive @Max(120) int age,
        @NotBlank @Size(min = 10, max = 36)String username,
        @NotBlank @Size(min = 10, max = 36) String password,
        @NotBlank @Email String email,
        @NotNull @Valid CreateAddressDto address
) {
}
