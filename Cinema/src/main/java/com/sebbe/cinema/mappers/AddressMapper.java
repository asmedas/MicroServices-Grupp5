package com.sebbe.cinema.mappers;

import com.sebbe.cinema.dtos.addressDtos.AddressDto;
import com.sebbe.cinema.entities.Address;

import java.util.Set;
import java.util.stream.Collectors;

public class AddressMapper {

    private AddressMapper(){
    }

    public static Set<AddressDto> toDtoSet(Set<Address> addresses){
        return addresses.stream()
                .map(address -> new AddressDto(address.getId(), address.getStreet(),
                        address.getCity(), address.getPostalCode()))
                .collect(Collectors.toSet());
    }
}
