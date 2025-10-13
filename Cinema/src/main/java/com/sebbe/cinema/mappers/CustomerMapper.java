package com.sebbe.cinema.mappers;

import com.sebbe.cinema.dtos.addressDto.AddressDto;
import com.sebbe.cinema.dtos.customerDtos.CreateCustomerWithAccountDto;
import com.sebbe.cinema.dtos.customerDtos.CustomerDto;
import com.sebbe.cinema.entities.Address;
import com.sebbe.cinema.entities.Customer;

import java.util.Set;

public class CustomerMapper {

    private CustomerMapper(){}

    public static CustomerDto toDto(Customer customer){
        Set<AddressDto> addressDto = AddressMapper.toDtoSet(customer.getAddress());
        return new CustomerDto(customer.getId(), customer.getFirstName(),
                customer.getLastName(), customer.getEmail(), addressDto);
    }

    public static Customer buildCustomerFromCreateDto(CreateCustomerWithAccountDto dto, String keycloakId, Address address) {
        Customer customer = new Customer(
                keycloakId,
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.age()
        );
        customer.addAddress(address);
        return customer;
    }

}
