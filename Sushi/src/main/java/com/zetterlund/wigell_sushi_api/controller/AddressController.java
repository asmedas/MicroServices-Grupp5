package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.AddressDto;
import com.zetterlund.wigell_sushi_api.entity.Address;
import com.zetterlund.wigell_sushi_api.exception.BadRequestException;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AddressDto> addAddress(@PathVariable Integer customerId, @RequestBody AddressDto addressDto) {
        if (addressDto.getStreet() == null || addressDto.getStreet().isEmpty()) {
            throw new BadRequestException("Street cannot be empty.");
        }

        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setPostalCode(addressDto.getPostalCode());

        Address createdAddress = addressService.addAddress(customerId, address);

        AddressDto result = new AddressDto();
        result.setStreet(createdAddress.getStreet());
        result.setCity(createdAddress.getCity());
        result.setPostalCode(createdAddress.getPostalCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer customerId, @PathVariable Integer addressId) {
        Address address = addressService
                .getAllAddresses()
                .stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " not found."));

        addressService.deleteAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}