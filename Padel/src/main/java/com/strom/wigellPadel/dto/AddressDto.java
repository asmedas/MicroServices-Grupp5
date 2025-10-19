package com.strom.wigellPadel.dto;

public record AddressDto (
        Long id,
        String street,
        String postalCode,
        String city
){
}
