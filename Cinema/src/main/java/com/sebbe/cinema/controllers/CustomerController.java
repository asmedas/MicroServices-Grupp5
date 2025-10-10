package com.sebbe.cinema.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listCustomers() {
        return "GET /api/v1/customers";
    }

    @PostMapping
    public String addCustomer() {
        return "POST /api/v1/customers";
    }

    @DeleteMapping("/{customerId}")
    public String deleteCustomer(@PathVariable Long customerId) {
        return "DELETE /api/v1/customers/" + customerId;
    }

    @PutMapping("/{customerId}")
    public String updateCustomer(@PathVariable Long customerId) {
        return "PUT /api/v1/customers/" + customerId;
    }

    @PostMapping("/{customerId}/addresses")
    public String addAddressToCustomer(@PathVariable Long customerId) {
        return "POST /api/v1/customers/" + customerId + "/addresses";
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public String deleteAddressFromCustomer(@PathVariable Long customerId, @PathVariable Long addressId) {
        return "DELETE /api/v1/customers/" + customerId + "/addresses/" + addressId;
    }

}
