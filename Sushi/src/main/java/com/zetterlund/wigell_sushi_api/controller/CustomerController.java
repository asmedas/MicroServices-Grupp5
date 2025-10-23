package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.CustomerCreationRequestDto;
import com.zetterlund.wigell_sushi_api.dto.CustomerDto;
import com.zetterlund.wigell_sushi_api.exception.BadRequestException;
import com.zetterlund.wigell_sushi_api.exception.ConflictException;
import com.zetterlund.wigell_sushi_api.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        logger.info("getAllCustomers");
        List<CustomerDto> dtos = customerService.getAllCustomers()
                .stream()
                .map(customerService::mapToDto) // Mappningsmetod i service
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody CustomerCreationRequestDto request) {
        logger.info("Received POST /customers with body: {}", request);

        // Validera obligatoriska fält
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new BadRequestException("Username is mandatory.");
        }
        if (request.getRawPassword() == null || request.getRawPassword().isEmpty()) {
            throw new BadRequestException("Password is mandatory.");
        }

        // Kontrollera om användarnamn redan finns
        if (customerService.getAllCustomers().stream()
                .anyMatch(c -> c.getUsername().equals(request.getUsername()))) {
            throw new ConflictException("Username " + request.getUsername() + " is already taken.");
        }

        // Skapa kund
        CustomerDto createdCustomer = customerService.addCustomerFromDto(request);

        // Returnera DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{customerId}")
    public CustomerDto updateCustomer(@PathVariable Integer customerId,
                                      @RequestBody CustomerCreationRequestDto dto) {
        return customerService.updateCustomer(customerId, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomerById(customerId);
        return ResponseEntity.noContent().build();
    }
}
