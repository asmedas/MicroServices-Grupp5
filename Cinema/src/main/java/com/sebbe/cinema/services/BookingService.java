package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.booking.CreateBookingDto;
import com.sebbe.cinema.dtos.booking.PatchBookingDto;
import com.sebbe.cinema.entities.*;
import com.sebbe.cinema.enums.TechnicalEquipment;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final CallCurrencyApiService callCurrencyApiService;
    private final CinemaHallRepository cinemaHallRepository;
    private final CustomerRepository customerRepository;
    private final FilmRepository filmRepository;

    public BookingService(BookingRepository bookingRepository, CallCurrencyApiService callCurrencyApiService
    , CinemaHallRepository cinemaHallRepository, CustomerRepository customerRepository,
                          FilmRepository filmRepository) {
        this.bookingRepository = bookingRepository;
        this.callCurrencyApiService = callCurrencyApiService;
        this.cinemaHallRepository = cinemaHallRepository;
        this.customerRepository = customerRepository;
        this.filmRepository = filmRepository;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Booking createBooking(CreateBookingDto createBookingDto) {
        log.debug("Creating booking for customer with keycloakId: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NoMatchException("Customer not found for current user"));
        CinemaHall cinemaHall = cinemaHallRepository.findById(createBookingDto.cinemaHallId())
                .orElseThrow(() -> {
                    log.error("Screening not found");
                    return new NoMatchException("Screening not found");
                });
        if(createBookingDto.filmId() != null && createBookingDto.speaker() != null){
            log.error("Can't have both a FilmID and a speaker");
            throw new IllegalArgumentException("Can't have both a FilmID and a speaker");
        }
        if(createBookingDto.filmId() == null && createBookingDto.speaker() == null){
            log.error("Must have either a FilmID or a speaker");
            throw new IllegalArgumentException("Must have either a FilmID or a speaker");
        }
        if(createBookingDto.filmId() != null){
            log.debug("Film id: {}", createBookingDto.filmId());
            Film film = filmRepository.findById(createBookingDto.filmId())
                    .orElseThrow(() -> {
                        log.error("Film not found");
                        return new NoMatchException("Film not found");
                    });
            Booking booking = new Booking(customer, cinemaHall,
                    film, createBookingDto.technicalEquipment(),
                    createBookingDto.date());
            booking.setTotalPriceSek(calculatePriceSek(cinemaHall));
            booking.setTotalPriceUsd(calculatePriceUsd(booking.getTotalPriceSek()));
            return bookingRepository.save(booking);
        }
        log.debug("Creating booking with speaker: {}", createBookingDto.speaker());
        Booking booking = new Booking(customer, cinemaHall,
                createBookingDto.speaker(), createBookingDto.technicalEquipment(),
                createBookingDto.date());
        booking.setTotalPriceSek(calculatePriceSek(cinemaHall));
        booking.setTotalPriceUsd(calculatePriceUsd(booking.getTotalPriceSek()));
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found");
                    return new NoMatchException("Booking not found");
                });
        booking.removeBookingFromConnections();
        try{
            bookingRepository.delete(booking);
        } catch (DataAccessException e){
            log.error("Error removing Booking", e);
            throw new UnexpectedError("Error removing Booking " + e);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Booking patchBookingById(Long bookingId, PatchBookingDto patchBookingDto){
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> {
                    log.error("Customer not found");
                    return new NoMatchException("Customer not found");
                });
        log.debug("Updating booking with id: {} for user {}", bookingId, customer.getId());
        customer.getBookings().stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new NoMatchException("Booking not found for customer"));
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found");
                    return new NoMatchException("Booking not found");
                });
        if(patchBookingDto.technicalEquipment() != null){
            existingBooking.setTechnicalEquipment(patchBookingDto.technicalEquipment());
        }
        if(patchBookingDto.date() != null){
            existingBooking.setDate(patchBookingDto.date());
        }
        return bookingRepository.save(existingBooking);
    }

    @PreAuthorize("hasRole('ROLE_USER') and @ownership.isSelf(authentication, #customerId)")
    public List<Booking> getBookingsByCustomerId(Long customerId){
        log.debug("Fetching bookings by customerId: {}", customerId);
        if(customerRepository.findById(customerId).isEmpty()){
            log.error("Customer not found");
            throw new NoMatchException("Customer not found");
        }
        return bookingRepository.findByCustomerId(customerId);
    }

    private BigDecimal calculatePriceSek(CinemaHall cinemaHall){
        return BigDecimal.valueOf(cinemaHall.getMaxSeats() * 100);
    }

    private BigDecimal calculatePriceUsd(BigDecimal priceSek){
        return callCurrencyApiService.convertFromSEKToUsd(priceSek);
    }

    public Booking commandLineRunnerFilm(CinemaHall cinemaHall, Customer customer, Film film, LocalDate date){
        Booking booking = new Booking(customer, cinemaHall, film,
                List.of(TechnicalEquipment.AUDIO, TechnicalEquipment.SCREEN, TechnicalEquipment.SUBTITLES), date);
        booking.setTotalPriceSek(calculatePriceSek(cinemaHall));
        booking.setTotalPriceUsd(calculatePriceUsd(booking.getTotalPriceSek()));
        return bookingRepository.save(booking);
    }

    public Booking commandLineRunnerSpeaker(CinemaHall cinemaHall, Customer customer, String speaker, LocalDate date){
        Booking booking = new Booking(customer, cinemaHall, speaker,
                List.of(TechnicalEquipment.AUDIO, TechnicalEquipment.SCREEN, TechnicalEquipment.SUBTITLES), date);
        booking.setTotalPriceSek(calculatePriceSek(cinemaHall));
        booking.setTotalPriceUsd(calculatePriceUsd(booking.getTotalPriceSek()));
        return bookingRepository.save(booking);
    }
}
