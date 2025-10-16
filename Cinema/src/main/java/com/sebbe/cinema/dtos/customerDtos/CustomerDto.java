package com.sebbe.cinema.dtos.customerDtos;

import com.sebbe.cinema.dtos.addressDtos.AddressDto;

import java.util.Set;

public record CustomerDto(
        long id,
        String firstName,
        String lastName,
        String email,
        Set<AddressDto> addressDto
) {
}
