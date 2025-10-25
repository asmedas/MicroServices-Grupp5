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

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final DestinationRepository destinationRepository;
    private final CurrencyService currencyService;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

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
        Customer customer = customerRepository.findById(req.customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + req.customerId));
        Destination dest = destinationRepository.findById(req.destinationId)
                .orElseThrow(() -> new EntityNotFoundException("Destination not found: " + req.destinationId));

        BigDecimal totalSek = dest.getPricePerWeekSek()
                .multiply(BigDecimal.valueOf(req.weeks))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPln = currencyService.convertSekToPln(totalSek);

        Booking b = new Booking(
                req.departureDate, req.weeks,
                dest.getHotelName(), customer, dest,
                totalSek, totalPln
        );
        Booking saved = bookingRepository.save(b);


        log.info("user with id={} created booking id={} for destination={} ({}, hotel={}) weeks={} totalPriceSek={} totalPricePln={}",
                customer.getId(),
                saved.getId(),
                dest.getCity(),
                dest.getCountry(),
                dest.getHotelName(),
                req.weeks,
                totalSek,
                totalPln);

        return toResponse(saved);
    }

    @Transactional
    public BookingResponse patch(Long id, BookingPatchRequest req) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found: " + id));

        if (req.hotelName != null) b.setHotelName(req.hotelName);
        if (req.destinationId != null) {
            Destination newDest = destinationRepository.findById(req.destinationId)
                    .orElseThrow(() -> new EntityNotFoundException("Destination not found: " + req.destinationId));
            b.setDestination(newDest);
            b.setHotelName(newDest.getHotelName());
        }
        if (req.weeks != null) {
            b.setWeeks(req.weeks);
        }

        // Recalculate prices
        BigDecimal totalSek = b.getDestination().getPricePerWeekSek()
                .multiply(BigDecimal.valueOf(b.getWeeks()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPln = currencyService.convertSekToPln(totalSek);
        b.setTotalPriceSek(totalSek);
        b.setTotalPricePln(totalPln);

        Booking saved = bookingRepository.save(b);


        log.info("user with id={} updated booking id={} -> new destination={} ({}) hotel={} weeks={} totalPriceSek={} totalPricePln={}",
                b.getCustomer().getId(),
                id,
                b.getDestination().getCity(),
                b.getDestination().getCountry(),
                b.getHotelName(),
                b.getWeeks(),
                totalSek,
                totalPln);

        return toResponse(saved);
    }

    @Transactional
    public List<BookingResponse> listByCustomer(String customerId) {
        log.debug("listing bookings for customerId={}", customerId);
        return bookingRepository.findByCustomer_Id(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getCustomer().getId(),
                b.getDestination().getId(),
                b.getDestination().getCity(),
                b.getDestination().getCountry(),
                b.getHotelName(),
                b.getDepartureDate(),
                b.getWeeks(),
                b.getTotalPriceSek(),
                b.getTotalPricePln()
        );
    }
}
