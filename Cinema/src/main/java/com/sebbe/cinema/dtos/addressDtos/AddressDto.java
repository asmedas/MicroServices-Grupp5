package com.sebbe.cinema.dtos.addressDtos;

public record AddressDto(
        long id,
        String street,
        String city,
        String postalCode
) {
}
