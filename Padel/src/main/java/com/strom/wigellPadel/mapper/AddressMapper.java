package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.AddressCreateDto;
import com.strom.wigellPadel.dto.AddressDto;
import com.strom.wigellPadel.entities.Address;

public class AddressMapper {

    public static AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }

    public static Address fromCreate(AddressCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Address newAddress = new Address(dto.street(), dto.postalCode(), dto.city());
        return newAddress;
    }
}
