package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.AddressDto;
import com.andreas.wigellmcrental.entity.Address;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    // ADMIN: Lista alla adresser
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AddressDto> all() {
        return service.findAll().stream().map(Mapper::toAddressDto).toList();
    }

    // USER/ADMIN: Lägg till adress för kund
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/customer/{customerId}")
    public AddressDto addAddress(@PathVariable Long customerId,
                                 @Valid @RequestBody AddressDto in,
                                 Authentication auth) {
        Address saved = service.addAddress(customerId, in, auth);
        return Mapper.toAddressDto(saved);
    }

    // USER/ADMIN: Uppdatera adress
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{addressId}")
    public AddressDto update(@PathVariable Long addressId,
                             @Valid @RequestBody AddressDto in,
                             Authentication auth) {
        return Mapper.toAddressDto(service.updateAddress(addressId, in, auth));
    }

    // USER/ADMIN: Ta bort adress
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{addressId}")
    public void delete(@PathVariable Long addressId, Authentication auth) {
        service.deleteAddress(addressId, auth);
    }
}
