package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.CustomerCreateDto;
import com.strom.wigellPadel.dto.CustomerDto;
import com.strom.wigellPadel.dto.CustomerUpdateDto;
import com.strom.wigellPadel.dto.CustomerWithAccountCreateDto;
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
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getUsername(),
                customer.getAddress(),
                customer.getKeycloakUserId()
        );
    }

    public static Customer fromCreate(CustomerCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Address address = new Address(dto.street(), dto.postalCode(), dto.city());
        Set<Address> addresses = new HashSet<>();
        addresses.add(address);
        Customer newCustomer = new Customer(dto.firstName(), dto.lastName(), addresses, dto.email(), dto.username(), dto.password());
        address.setCustomers(new HashSet<>());
        address.getCustomers().add(newCustomer);
        return newCustomer;
    }

    public static void updateEntity(Customer customer, CustomerUpdateDto dto) {
        if (dto == null || customer == null) {
            return;
        }
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        Address address = new Address(dto.street(), dto.postalCode(), dto.city());
        Set<Address> addresses = customer.getAddress() != null ? customer.getAddress() : new HashSet<>();
        addresses.add(address);
        customer.setAddress(addresses);
        if (address.getCustomers() == null) {
            address.setCustomers(new HashSet<>());
        }
        address.getCustomers().add(customer);
    }
}
