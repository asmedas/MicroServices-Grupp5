package com.strom.wigellPadel.controller;

import com.strom.wigellPadel.dto.AvailableDto;
import com.strom.wigellPadel.dto.BookingCreateDto;
import com.strom.wigellPadel.dto.BookingDto;
import com.strom.wigellPadel.dto.BookingUpdateDto;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import com.strom.wigellPadel.services.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final BookingService bookingService;
    private final CustomerRepo customerRepo;
    private final BookingRepo bookingRepo;

    public UserController(BookingService bookingService, CustomerRepo customerRepo, BookingRepo bookingRepo) {
        this.bookingService = bookingService;
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
    }

    private String getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getClaimAsString("sub");
        if (userId == null) {
            logger.error("Kunde inte hämta user ID från JWT token");
            throw new AccessDeniedException("User ID hittades inte i JWT token");
        }
        logger.debug("Authenticated user ID från JWT: {}", userId);
        return userId;
    }

    private boolean hasAdminRole() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            boolean isAdmin = roles.contains("ADMIN");
            logger.debug("User har ADMIN role: {}", isAdmin);
            return isAdmin;
        }
        logger.debug("Inga realm_access roles hittades i JWT, user är inte ADMIN");
        return false;
    }

    private String getKeycloakUserId(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Kund med id {} hittades inte", customerId);
                    return new EntityNotFoundException("Kund med id " + customerId + " hittades inte");
                });
        String keycloakUserId = customer.getKeycloakUserId();
        if (keycloakUserId == null) {
            logger.error("Keycloak user ID är null för kund med id {}", customerId);
            throw new AccessDeniedException("Keycloak user ID hittades inte för kund");
        }
        logger.debug("Keycloak user ID for customerId {}: {}", customerId, keycloakUserId);
        return keycloakUserId;
    }

    private Long getCustomerIdByBookingId(Long bookingId) {
        return bookingRepo.findById(bookingId)
                .map(booking -> {
                    logger.debug("Hittade bokning med ID {} och kund-ID {}", bookingId, booking.getCustomerId());
                    return booking.getCustomerId();
                })
                .orElseThrow(() -> {
                    logger.error("Bokning med id {} hittades inte", bookingId);
                    return new EntityNotFoundException("Bokning med id " + bookingId + " hittades inte");
                });
    }

    @GetMapping("/availability")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AvailableDto>> getAvailableSlots(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam Long courtId) {
        logger.info("Mottog begäran om att hämta tillgängliga tider för datum {} och bana {}", date, courtId);
        List<AvailableDto> availableSlots = bookingService.availableTimeSlots(date, courtId);
        logger.debug("Returnerar {} tillgängliga tider för datum {} och bana {}", availableSlots.size(), date, courtId);
        return new ResponseEntity<>(availableSlots, HttpStatus.OK);
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingCreateDto dto) {
        logger.info("Mottog begäran om att skapa bokning för kund-ID {}", dto.customerId());
        if (!hasAdminRole()) {
            String userId = getAuthenticatedUserId();
            String keycloakUserId = getKeycloakUserId(dto.customerId());
            if (!userId.equals(keycloakUserId)) {
                logger.warn("Unauthorized försök att skapa bokning för kund-ID {} av user {}. Förväntat keycloakUserId: {}, verkligt userId: {}",
                        dto.customerId(), userId, keycloakUserId, userId);
                throw new AccessDeniedException("Du är inte behörig att skapa bokning för denna kund");
            }
        } else {
            logger.debug("Bypassing keycloakUserId check för ADMIN role i createBooking för kund-ID {}", dto.customerId());
        }
        BookingDto bookingDto = bookingService.createBooking(dto);
        logger.debug("Skapade bokning med ID {} för kund-ID {}", bookingDto.id(), dto.customerId());
        return ResponseEntity.created(URI.create("/api/v1/bookings/" + bookingDto.id())).body(bookingDto);
    }

    @PatchMapping("/bookings/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDto> patchBooking(@PathVariable Long bookingId, @RequestBody BookingUpdateDto dto) {
        logger.info("Mottog begäran om att uppdatera bokning med ID {}", bookingId);
        if (!hasAdminRole()) {
            String userId = getAuthenticatedUserId();
            Long customerId = getCustomerIdByBookingId(bookingId); // Use helper method
            String keycloakUserId = getKeycloakUserId(customerId);
            if (!userId.equals(keycloakUserId)) {
                logger.warn("Unauthorized försök att uppdatera bokning {} av user {}. Förväntat keycloakUserId: {}, verkligt userId: {}",
                        bookingId, userId, keycloakUserId, userId);
                throw new AccessDeniedException("Du är inte behörig att uppdatera denna bokning");
            }
        } else {
            logger.debug("Bypassing keycloakUserId check för ADMIN role i patchBooking för bokning med ID {}", bookingId);
        }
        BookingDto updatedBooking = bookingService.patchBooking(bookingId, dto);
        logger.debug("Uppdaterade bokning med ID {}", bookingId);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    @GetMapping(value = "/bookings", params = "customerId")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingDto>> getBookings(@RequestParam Long customerId) {
        logger.info("Mottog begäran om att hämta bokningar för kund-ID {}", customerId);
        if (!hasAdminRole()) {
            String userId = getAuthenticatedUserId();
            String keycloakUserId = getKeycloakUserId(customerId);
            if (!userId.equals(keycloakUserId)) {
                logger.warn("Unauthorized försök att hämta bokningar för kund-ID {} av user {}. Förväntat keycloakUserId: {}, Verkligt userId: {}",
                        customerId, userId, keycloakUserId, userId);
                throw new AccessDeniedException("Du är inte behörig att visa bokningar för denna kund");
            }
        } else {
            logger.debug("Bypassing keycloakUserId check för ADMIN role i getBookings för kund-ID {}", customerId);
        }
        List<BookingDto> bookingList = bookingService.getBookingsByCustomerId(customerId);
        logger.debug("Returnerar {} bokningar för kund-ID {}", bookingList.size(), customerId);
        return new ResponseEntity<>(bookingList, HttpStatus.OK);
    }
}