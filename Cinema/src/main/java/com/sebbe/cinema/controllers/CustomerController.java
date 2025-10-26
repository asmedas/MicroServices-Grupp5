package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.addressDtos.CreateAddressDto;
import com.sebbe.cinema.dtos.customerDtos.CreateCustomerWithAccountDto;
import com.sebbe.cinema.dtos.customerDtos.CustomerDto;
import com.sebbe.cinema.dtos.customerDtos.CustomerUpdateDto;
import com.sebbe.cinema.entities.Customer;
import com.sebbe.cinema.services.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final Logger log = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CustomerDto>> listCustomers() {
        return ResponseEntity.ok(customerService.listCustomers());
    }

    /**
     * {
     *     "firstName": "sebbe",
     *     "lastName": "jonsson",
     *     "age": 30,
     *     "username": "sebbesuser",
     *     "password": "sebbespassword",
     *     "email": "sebbe@email.se",
     *     "address": {
         *     "street": "hårdvallsgatan 18",
         *     "city": "Sundsvall",
         *     "postalCode": "11456"
     *   }
     * }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid CreateCustomerWithAccountDto createCustomerWithAccountDto) {
        log.info("Creating customer with account: {}", createCustomerWithAccountDto);
        return ResponseEntity.created(URI.create("api/v1/customers"))
                .body(customerService.createCustomerWithKeycloakUserAndAddress(createCustomerWithAccountDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("User authorities: {}", auth.getAuthorities());
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {
     *     "firstName": "sebbe",
     *     "lastName": "jonsson",
     *     "email": "sebbeasd@gmail.com",
     *     "age": 35
     * }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody @Valid CustomerUpdateDto customerUpdateDto) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerUpdateDto));
    }

    /**
     * {
     *     "address": {
     *          "street": "hårdvallsgatan 18",
     *          "city": "Sundsvall",
     *          "postalCode": "11456"
     *        }
     * }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<CustomerDto> addAddressToCustomer(
            @PathVariable Long customerId,
            @RequestBody @Valid CreateAddressDto addressDto) {
        return ResponseEntity.ok(customerService.addAddress(customerId, addressDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> deleteAddressFromCustomer(@PathVariable Long customerId, @PathVariable Long addressId) {
        customerService.removeAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }

}
