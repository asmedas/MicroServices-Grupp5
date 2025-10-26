package com.miriam.travel.service;

import com.miriam.travel.dto.booking.BookingPatchRequest;
import com.miriam.travel.dto.booking.BookingRequest;
import com.miriam.travel.dto.booking.BookingResponse;
import com.miriam.travel.entity.Booking;
import com.miriam.travel.entity.Customer;
import com.miriam.travel.entity.Destination;
import com.miriam.travel.repository.BookingRepository;
import com.miriam.travel.repository.CustomerRepository;
import com.miriam.travel.repository.DestinationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final DestinationRepository destinationRepository;
    private final CurrencyService currencyService;

    public BookingService(BookingRepository bookingRepository,
                          CustomerRepository customerRepository,
                          DestinationRepository destinationRepository,
                          CurrencyService currencyService) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.destinationRepository = destinationRepository;
        this.currencyService = currencyService;
    }

    @Transactional
    public BookingResponse create(BookingRequest req) {
        log.info("Creating booking for customerId={} and destinationId={}", req.customerId, req.destinationId);

        Customer customer = customerRepository.findById(req.customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found: {}", req.customerId);
                    return new EntityNotFoundException("Customer not found: " + req.customerId);
                });

        Destination dest = destinationRepository.findById(req.destinationId)
                .orElseThrow(() -> {
                    log.error("not found: {}", req.destinationId);
                    return new EntityNotFoundException("Destination not found: " + req.destinationId);
                });

        BigDecimal totalSek = dest.getPricePerWeekSek()
                .multiply(BigDecimal.valueOf(req.weeks))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPln = currencyService.convertSekToPln(totalSek);

        Booking booking = new Booking(
                req.departureDate,
                req.weeks,
                dest.getHotelName(),
                customer,
                dest,
                totalSek,
                totalPln
        );

        Booking saved = bookingRepository.save(booking);

        log.info("Booking created successfully: id={}, destination={}, weeks={}, SEK={}, PLN={}",
                saved.getId(), dest.getCity(), req.weeks, totalSek, totalPln);

        return toResponse(saved);
    }

    @Transactional
    public BookingResponse patch(Long id, BookingPatchRequest req) {
        log.info("Updating booking id={}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Booking not found: {}", id);
                    return new EntityNotFoundException("Booking not found: " + id);
                });

        if (req.hotelName != null) {
            log.debug("Updating hotel name to {}", req.hotelName);
            booking.setHotelName(req.hotelName);
        }

        if (req.destinationId != null) {
            Destination newDest = destinationRepository.findById(req.destinationId)
                    .orElseThrow(() -> {
                        log.error("Destination not found: {}", req.destinationId);
                        return new EntityNotFoundException("Destination not found: " + req.destinationId);
                    });
            booking.setDestination(newDest);
            booking.setHotelName(newDest.getHotelName());
        }

        if (req.weeks != null) {
            log.debug("Updating weeks to {}", req.weeks);
            booking.setWeeks(req.weeks);
        }

        BigDecimal totalSek = booking.getDestination().getPricePerWeekSek()
                .multiply(BigDecimal.valueOf(booking.getWeeks()))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPln = currencyService.convertSekToPln(totalSek);
        booking.setTotalPriceSek(totalSek);
        booking.setTotalPricePln(totalPln);

        Booking saved = bookingRepository.save(booking);

        log.info("Booking updated successfully: id={}, newDestination={}, SEK={}, PLN={}",
                id, booking.getDestination().getCity(), totalSek, totalPln);

        return toResponse(saved);
    }

    @Transactional
    public List<BookingResponse> listByCustomer(String customerId) {
        log.info("Fetching all bookings for customerId={}", customerId);
        return bookingRepository.findByCustomer_Id(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getDestination().getId(),
                booking.getDestination().getCity(),
                booking.getDestination().getCountry(),
                booking.getHotelName(),
                booking.getDepartureDate(),
                booking.getWeeks(),
                booking.getTotalPriceSek(),
                booking.getTotalPricePln()
        );
    }
}

