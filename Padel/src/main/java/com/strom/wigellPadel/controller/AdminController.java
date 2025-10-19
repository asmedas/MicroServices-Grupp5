package com.strom.wigellPadel.controller;

import com.strom.wigellPadel.dto.*;
import com.strom.wigellPadel.mapper.BookingMapper;
import com.strom.wigellPadel.services.AddressService;
import com.strom.wigellPadel.services.BookingService;
import com.strom.wigellPadel.services.CourtService;
import com.strom.wigellPadel.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final CustomerService customerService;
    private final AddressService addressService;
    private final BookingService bookingService;
    private final CourtService courtService;

    public AdminController(CustomerService customerService,
                           AddressService addressService,
                           BookingService bookingService,
                           CourtService courtService) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.bookingService = bookingService;
        this.courtService = courtService;
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        logger.info("Mottog begäran om att hämta alla kunder");
        List<CustomerDto> customerList = customerService.getAllCustomers();
        logger.debug("Returnerar {} kunder", customerList.size());
        return new ResponseEntity<>(customerList, HttpStatus.OK);
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCreateDto dto) {
        logger.info("Mottog begäran om att skapa en ny kund");
        CustomerDto customerDto = customerService.createCustomer(dto);
        logger.debug("Skapade ny kund med id {}", customerDto.id());
        return new ResponseEntity<>(customerDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/customers/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer (@PathVariable Long customerId) {
        logger.info("Mottog begäran om att ta bort kund med id {}", customerId);
        customerService.deleteCustomer(customerId);
        logger.debug("Tog bort kund med id {}", customerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/customers/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerUpdateDto dto) {
        logger.info("Mottog begäran om att uppdatera kund med id {}", customerId);
        CustomerDto customerDto = customerService.updateCustomer(customerId, dto);
        logger.debug("Uppdaterade kund med id {}", customerId);
        return new ResponseEntity<>(customerDto, HttpStatus.OK);
    }

    @GetMapping("/courts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourtDto>> getCourts() {
        logger.info("Mottog begäran om att hämta alla padelbanor");
        List<CourtDto> courtList = courtService.getAllCourts();
        logger.debug("Returnerar {} padelbanor", courtList.size());
        return new ResponseEntity<>(courtList, HttpStatus.OK);
    }

    @GetMapping("/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDto> getCourt(@PathVariable Long courtId) {
        logger.info("Mottog begäran om att hämta padelbana md id {}", courtId);
        CourtDto courtDto = courtService.getCourt(courtId);
        logger.debug("Returnerar padelbana med id {}", courtDto.id());
        return new ResponseEntity<>(courtDto, HttpStatus.OK);
    }

    @PostMapping("courts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDto> createCourt(@RequestBody CourtCreateDto dto) {
        logger.info("Mottog begäran om att skapa en ny padelbana");
        CourtDto newCourt = courtService.createCourt(dto);
        logger.debug("Skapade ny padelbana med id {}", newCourt.id());
        return new ResponseEntity<>(newCourt, HttpStatus.CREATED);
    }

    @PutMapping("/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDto> updateCourt(@PathVariable Long courtId, @RequestBody CourtUpdateDto dto) {
        logger.info("Mottog begäran om att uppdatera padelbana med id {}", courtId);
        CourtDto updatedCourt = courtService.updateCourt(courtId, dto);
        logger.debug("Uppdaterade padelbana med id {}", updatedCourt.id());
        return new ResponseEntity<>(updatedCourt, HttpStatus.OK);
    }

    @DeleteMapping("/courts/{courtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourt(@PathVariable Long courtId) {
        logger.info("Mottog begäran om att ta bort padelbana med id {}", courtId);
        courtService.deleteCourt(courtId);
        logger.debug("Tog bort padelbana med id {}", courtId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bookings/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
        logger.info("Mottog begäran om att ta bort bokning med id {}", bookingId);
        bookingService.deleteBooking(bookingId);
        logger.debug("Tog bort bokning med id {}", bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDto>> getBookings() {
        logger.info("Mottog begäran om att hämta alla bokningar");
        List<BookingDto> bookingList = bookingService.getAllBookings();
        logger.debug("Returnerar {} bokningar", bookingList.size());
        return new ResponseEntity<>(bookingList, HttpStatus.OK);
    }

    @GetMapping("/bookings/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId) {
        logger.info("Mottog begäran om att hämta bokning med id {}", bookingId);
        BookingDto bookingDto = bookingService.getBooking(bookingId);
        logger.debug("Returnerar bokning med id {}", bookingDto.id());
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @PostMapping("/customers/{customerId}/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> createAddress(@PathVariable Long customerId, @RequestBody AddressCreateDto dto) {
        logger.info("Mottog begäran om att skapa en ny adress till kund med id {}", customerId);
        CustomerDto customer = addressService.createAddress(customerId, dto);
        logger.debug("Skapade ny adress till kund med id {} ", customer.id());
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @DeleteMapping("/customers/{customerId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        logger.info("Mottog begäran om att ta bort adress med id {} från kund med id {}", addressId, customerId);
        addressService.deleteAddressFromCustomer(customerId, addressId);
        logger.debug("Tog bort adress med id {} från kund med id {}", addressId, customerId);
        return ResponseEntity.noContent().build();
    }

}
