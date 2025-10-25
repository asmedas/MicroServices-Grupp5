package com.miriam.travel.controller;

import com.miriam.travel.dto.customer.CustomerCreateRequest;
import com.miriam.travel.dto.customer.CustomerSummaryDTO;
import com.miriam.travel.dto.customer.CustomerUpdateRequest;
import com.miriam.travel.entity.Address;
import com.miriam.travel.entity.Customer;
import com.miriam.travel.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerSummaryDTO>> list() {
        String user = getCurrentUser();
        log.info("{} listed all customers", user);
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Customer> create(@Valid @RequestBody CustomerCreateRequest req,
                                           UriComponentsBuilder uriBuilder) {
        Customer saved = service.create(req);
        URI location = uriBuilder.path("/api/v1/customers/{id}").buildAndExpand(saved.getId()).toUri();
        String user = getCurrentUser();
        log.info("{} created customer id={} username={}", user, saved.getId(), saved.getUsername());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Customer> update(@PathVariable String customerId,
                                           @Valid @RequestBody CustomerUpdateRequest req) {
        Customer updated = service.update(customerId, req);
        String user = getCurrentUser();
        log.info("{} updated customer id={} username={}", user, customerId, updated.getUsername());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String customerId) {
        service.delete(customerId);
        String user = getCurrentUser();
        log.info("{} deleted customer id={}", user, customerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Address> addAddress(@PathVariable String customerId,
                                              @Valid @RequestBody Address address,
                                              UriComponentsBuilder uriBuilder) {
        Address saved = service.addAddress(customerId, address);
        URI location = uriBuilder.path("/api/v1/customers/{cid}/addresses/{aid}")
                .buildAndExpand(customerId, saved.getId()).toUri();
        String user = getCurrentUser();
        log.info("{} added address id={} to customer id={}", user, saved.getId(), customerId);
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable String customerId,
                                              @PathVariable Long addressId) {
        service.deleteAddress(customerId, addressId);
        String user = getCurrentUser();
        log.info("{} deleted address id={} from customer id={}", user, addressId, customerId);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}
