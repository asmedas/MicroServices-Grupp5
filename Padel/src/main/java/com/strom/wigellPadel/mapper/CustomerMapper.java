package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.CustomerDto;
import com.strom.wigellPadel.dto.CustomerUpdateDto;
import com.strom.wigellPadel.dto.CustomerWithAccountCreateDto;
import com.strom.wigellPadel.entities.Customer;

public class CustomerMapper {

    public CustomerMapper() {
    }

    public static CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getUsername(),
                customer.getAddress(),
                customer.getKeycloakUserId()
        );
    }

    public static Customer fromCreate(CustomerDto dto) {
        if (dto == null) {
            return null;
        }
        Customer newCustomer = new Customer(dto.firstName(), dto.lastName(), dto.username(), dto.address());
        return newCustomer;
    }

    public static Customer fromCreateWithAccount(CustomerWithAccountCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Customer c = new Customer(dto.firstName(), dto.lastName(), dto.username());
        return c;
    }

    public static void updateEntity(Customer customer, CustomerUpdateDto dto) {
        if (dto == null) {
            return;
        }
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setAddress(dto.address());
    }
}
