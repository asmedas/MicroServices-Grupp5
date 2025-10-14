package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.entity.Address;
import com.zetterlund.wigell_sushi_api.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@PathVariable Integer customerId, @RequestBody Address address) {
        Address createdAddress = addressService.addAddress(customerId, address);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer customerId, @PathVariable Integer addressId) {
        addressService.deleteAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}