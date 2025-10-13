package com.sebbe.cinema.dtos.addressDto;

public record AddressDto(
        long id,
        String street,
        String city,
        String postalCode
) {
}
