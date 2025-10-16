package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.CustomerCreationRequestDto;
import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.exception.BadRequestException;
import com.zetterlund.wigell_sushi_api.exception.ConflictException;
import com.zetterlund.wigell_sushi_api.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody CustomerCreationRequestDto request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new BadRequestException("Username is mandatory.");
        }

        if (customerService.getAllCustomers().stream()
                .anyMatch(c -> c.getUsername().equals(request.getUsername()))) {
            throw new ConflictException("Username " + request.getUsername() + " is already taken.");
        }

        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setName(request.getName());

        Customer createdCustomer = customerService.addCustomer(customer, request.getRawPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Integer customerId,
            @RequestBody CustomerCreationRequestDto customerDto) {

        Customer updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomerById(customerId);
        return ResponseEntity.noContent().build();
    }
}
