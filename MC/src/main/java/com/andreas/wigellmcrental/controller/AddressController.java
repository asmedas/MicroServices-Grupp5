package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.AddressDto;
import com.andreas.wigellmcrental.entity.Address;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    // ADMIN: Lista alla adresser
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/addresses")
    public List<AddressDto> all() {
        return service.findAll().stream().map(Mapper::toAddressDto).toList();
    }

    // USER/ADMIN: Lägg till adress för kund
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<Map<String, Object>> addAddress(@PathVariable Long customerId,
                                                          @Valid @RequestBody AddressDto in,
                                                          Authentication auth) {
        Address saved = service.addAddress(customerId, in, auth);
        return message("Address created successfully for customer " + customerId,
                Mapper.toAddressDto(saved),
                HttpStatus.CREATED);
    }

    // USER/ADMIN: Uppdatera adress
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long addressId,
                                                      @Valid @RequestBody AddressDto in,
                                                      Authentication auth) {
        Address updated = service.updateAddress(addressId, in, auth);
        return message("Address " + addressId + " updated successfully",
                Mapper.toAddressDto(updated),
                HttpStatus.OK);
    }

    // USER/ADMIN: Ta bort adress
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/customers/{customerId}/addresses/{addressId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long customerId,
                                                      @PathVariable Long addressId,
                                                      Authentication auth) {
        service.deleteAddress(addressId, auth);
        return message("Address " + addressId + " deleted successfully for customer " + customerId,
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
