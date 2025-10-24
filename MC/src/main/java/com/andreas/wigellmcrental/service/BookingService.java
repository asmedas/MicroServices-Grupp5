package com.andreas.wigellmcrental.service;

import com.andreas.wigellmcrental.entity.*;
import com.andreas.wigellmcrental.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepo;
    private final CustomerRepository customerRepo;
    private final BikeRepository bikeRepo;
    private final WebClient webClient;

    @Value("${converter.service.url}")
    private String converterUrl;

    public BookingService(BookingRepository bookingRepo, CustomerRepository customerRepo, BikeRepository bikeRepo) {
        this.bookingRepo = bookingRepo;
        this.customerRepo = customerRepo;
        this.bikeRepo = bikeRepo;
        this.webClient = WebClient.create();
    }

    public List<Booking> all() {
        logger.info("Getting all bookings");
        List<Booking> bookings = bookingRepo.findAll();
        logger.info("Found {} bookings", bookings.size());
        return bookingRepo.findAll();
    }

    public Booking getBookingById(Long id) {
        logger.info("Getting booking by ID {}", id);
        Optional<Booking> booking = bookingRepo.findById(id);
        if (booking.isPresent()) {
            logger.info("Found booking by ID {}", id);
            } else {
            logger.info("Booking not found {}", id);
        }
        return bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> byCustomer(Long customerId) {
        return bookingRepo.findByCustomer_Id(customerId);
    }

    public Booking create(Long customerId, Long bikeId, LocalDate start, LocalDate end) {
        Customer c = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Bike b = bikeRepo.findById(bikeId)
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        // Kontrollera överlappande bokningar
        List<Booking> overlapping = bookingRepo.findOverlapping(bikeId, start, end);
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException(
                    "Motorcykeln " + b.getModel() + " är redan bokad mellan " +
                            overlapping.get(0).getStartDate() + " och " + overlapping.get(0).getEndDate()
            );

        }

        long days = ChronoUnit.DAYS.between(start, end); // för att räkna dagar
        double totalSek = days * b.getPricePerDay();
        double totalGbp = convertToGbp(totalSek);

        b.setAvailable(false);
        bikeRepo.save(b);

        Booking booking = new Booking();
        booking.setCustomer(c);
        booking.setBike(b);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setTotalPriceSek(totalSek);
        booking.setTotalPriceGbp(totalGbp);
        booking.setStatus(BookingStatus.ACTIVE);

        logger.info("Booking created: customer={}, bike={}, start={}, end={}",
                c.getUsername(), b.getModel(), start, end);

        return bookingRepo.save(booking);
    }

    public Booking setStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);

        if (status == BookingStatus.RETURNED || status == BookingStatus.CANCELLED) {
            Bike bike = booking.getBike();
            bike.setAvailable(true);
            bikeRepo.save(bike);
        }

        logger.info("Booking status changed: id={}, status={}", id, status);
        return bookingRepo.save(booking);
    }

    public void delete(Long id) {
        Booking b = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Bike bike = b.getBike();
        if (bike != null) {
            bike.setAvailable(true);
            bikeRepo.save(bike);
        }
        bookingRepo.delete(b);
        logger.info("Booking deleted: id={}", id);
    }

    public Booking replace(Long id, Long customerId, Long bikeId, LocalDate start, LocalDate end) {
        Booking existing = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Customer c = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Bike newBike = bikeRepo.findById(bikeId)
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        Bike oldBike = existing.getBike();
        if (oldBike != null && !oldBike.getId().equals(newBike.getId())) {
            oldBike.setAvailable(true);
            bikeRepo.save(oldBike);
            newBike.setAvailable(false);
            bikeRepo.save(newBike);
        }

        long days = ChronoUnit.DAYS.between(start, end);
        double totalSek = days * newBike.getPricePerDay();
        double totalGbp = convertToGbp(totalSek);

        existing.setCustomer(c);
        existing.setBike(newBike);
        existing.setStartDate(start);
        existing.setEndDate(end);
        existing.setTotalPriceSek(totalSek);
        existing.setTotalPriceGbp(totalGbp);
        existing.setStatus(BookingStatus.ACTIVE);

        logger.info("Booking replaced: id={}, customer={}, bike={}",
                id, c.getUsername(), newBike.getModel());

        return bookingRepo.save(existing);
    }

    public Booking patch(Long id, Long bikeId, LocalDate start, LocalDate end,
                         BookingStatus status, boolean isAdmin) {
        Booking existing = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        boolean recomputePrice = false;

        if (bikeId != null && !bikeId.equals(existing.getBike().getId())) {
            Bike newBike = bikeRepo.findById(bikeId)
                    .orElseThrow(() -> new RuntimeException("Bike not found"));
            Bike oldBike = existing.getBike();
            if (oldBike != null && !oldBike.getId().equals(newBike.getId())) {
                oldBike.setAvailable(true);
                bikeRepo.save(oldBike);
                newBike.setAvailable(false);
                bikeRepo.save(newBike);
            }
            existing.setBike(newBike);
            recomputePrice = true;
        }

        LocalDate newStart = start != null ? start : existing.getStartDate();
        LocalDate newEnd = end != null ? end : existing.getEndDate();
        if (start != null || end != null) {
            existing.setStartDate(newStart);
            existing.setEndDate(newEnd);
            recomputePrice = true;
        }

        if (isAdmin && status != null) {
            existing.setStatus(status);
            if (status == BookingStatus.RETURNED || status == BookingStatus.CANCELLED) {
                Bike bike = existing.getBike();
                bike.setAvailable(true);
                bikeRepo.save(bike);
            }
        }

        if (recomputePrice) {
            long days = ChronoUnit.DAYS.between(existing.getStartDate(), existing.getEndDate());
            double totalSek = days * existing.getBike().getPricePerDay();
            double totalGbp = convertToGbp(totalSek);
            existing.setTotalPriceSek(totalSek);
            existing.setTotalPriceGbp(totalGbp);
        }

        logger.info("Booking patched: id={}, admin={}, status={}", id, isAdmin, status);
        return bookingRepo.save(existing);
    }

    public Long getCustomerIdByUsername(String username) {
        return customerRepo.findAll().stream()
                .filter(c -> c.getUsername().equals(username))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow(() -> new RuntimeException("Customer not found for username: " + username));
    }

    public double convertToGbp(double amountSek) {
        try {
            // 👇 Ändring här — tvingar punkt som decimal
            String url = String.format(Locale.US, "%s/api/convert?to=GBP&amount=%.2f", converterUrl, amountSek);
            logger.info("Calling converter service: {}", url);

            Double converted = webClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer 123456789")
                    .retrieve()
                    .bodyToMono(Double.class)
                    .doOnError(err -> logger.error("Converter call failed: {}", err.getMessage()))
                    .block();

            if (converted == null) {
                logger.error("Converter returned null response");
                return 0.0;
            }

            logger.info("Converted {} SEK → {} GBP", amountSek, converted);
            return converted;

        } catch (Exception e) {
            logger.error("Currency conversion failed for amountSek={}", amountSek, e);
            return 0.0;
        }
    }


}
