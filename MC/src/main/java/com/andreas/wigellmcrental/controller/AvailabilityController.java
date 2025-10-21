package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.BikeDto;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.AvailabilityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    // Alla inloggade får se lediga motorcyklar
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/availability")
    public List<BikeDto> getAvailable(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return service.getAvailable(from, to).stream()
                .map(Mapper::toBikeDto)
                .toList();
    }
}
