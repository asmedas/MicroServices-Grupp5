package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.*;
import com.strom.wigellPadel.entities.Address;
import com.strom.wigellPadel.entities.Customer;

import java.util.HashSet;
import java.util.Set;

public class CustomerMapper {

    public CustomerMapper() {
    }

    public static CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        Set<AddressDto> addressDtoList = new HashSet<>();
        for (Address address : customer.getAddress()) {
            addressDtoList.add(new AddressDto(address.getId(), address.getStreet(), address.getPostalCode(), address.getCity()));
        }
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getUsername(),
                addressDtoList,
                customer.getKeycloakUserId()
        );
    }

}
