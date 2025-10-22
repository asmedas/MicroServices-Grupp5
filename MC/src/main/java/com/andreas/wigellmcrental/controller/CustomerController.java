package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.CustomerDto;
import com.andreas.wigellmcrental.entity.Customer;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ADMIN: skapa kund
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CustomerDto in) {
        Customer saved = service.create(new Customer(in.username(), in.name(), in.email(), in.phone()));
        return message("Customer created successfully",
                Mapper.toCustomerDto(saved),
                HttpStatus.CREATED);
    }

    // ADMIN: uppdatera kund
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/customers/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                      @Valid @RequestBody CustomerDto in) {
        Customer updated = service.update(id, new Customer(in.username(), in.name(), in.email(), in.phone()));
        return message("Customer " + id + " updated successfully",
                Mapper.toCustomerDto(updated),
                HttpStatus.OK);
    }

    // ADMIN: ta bort kund
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        service.delete(id);
        return message("Customer " + id + " deleted successfully",
                null,
                HttpStatus.OK);
    }

    // Hjälpmetod för enhetliga svar
    private ResponseEntity<Map<String, Object>> message(String msg, Object data, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", msg);
        if (data != null) body.put("data", data);
        return ResponseEntity.status(status).body(body);
    }
}
