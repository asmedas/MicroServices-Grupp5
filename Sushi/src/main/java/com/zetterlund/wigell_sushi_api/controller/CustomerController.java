package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.CustomerCreationRequest;
import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody CustomerCreationRequest request) {
        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setName(request.getName());

        Customer createdCustomer = customerService.addCustomer(customer, request.getRawPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }
}
