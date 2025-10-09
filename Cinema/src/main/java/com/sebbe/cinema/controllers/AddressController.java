package com.sebbe.cinema.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    @GetMapping
    public String listAddresses() {
        return "GET /api/v1/addresses";
    }

    @PostMapping
    public String createAddress() {
        return "POST /api/v1/addresses";
    }

    @DeleteMapping("/{addressId}")
    public String deleteAddress(@PathVariable Long addressId) {
        return "DELETE /api/v1/addresses/" + addressId;
    }

}
