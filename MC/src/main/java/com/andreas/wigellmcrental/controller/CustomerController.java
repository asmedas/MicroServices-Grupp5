package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.AddressDto;
import com.andreas.wigellmcrental.dto.CustomerDto;
import com.andreas.wigellmcrental.entity.Address;
import com.andreas.wigellmcrental.entity.Customer;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // ADMIN: lista alla kunder
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers")
    public List<CustomerDto> all() {
        return service.findAll().stream().map(Mapper::toCustomerDto).toList();
    }

    // ADMIN: hämta specifik kund
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers/{id}")
    public CustomerDto get(@PathVariable Long id) {
        return Mapper.toCustomerDto(service.get(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers")
    public CustomerDto create(@Valid @RequestBody CustomerDto in) {
        Customer saved = service.create(new Customer(in.username(), in.name(), in.email(), in.phone()));
        return Mapper.toCustomerDto(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/customers/{id}")
    public CustomerDto update(@PathVariable Long id, @Valid @RequestBody CustomerDto in) {
        Customer updated = service.update(id, new Customer(in.username(), in.name(), in.email(), in.phone()));
        return Mapper.toCustomerDto(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/customers/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
