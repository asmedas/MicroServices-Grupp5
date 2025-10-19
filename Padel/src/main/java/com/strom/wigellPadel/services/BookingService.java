package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.*;
import com.strom.wigellPadel.entities.Booking;
import com.strom.wigellPadel.entities.Court;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.mapper.AvailableMapper;
import com.strom.wigellPadel.mapper.BookingMapper;
import com.strom.wigellPadel.mapper.CourtMapper;
import com.strom.wigellPadel.mapper.CustomerMapper;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CourtRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepo bookingRepo;
    private final CourtRepo courtRepo;
    private final CustomerRepo customerRepo;
    private final CurrencyConverterClient converterClient;

    public BookingService(BookingRepo bookingRepo, CourtRepo courtRepo, CustomerRepo customerRepo, CurrencyConverterClient converterClient) {
        this.bookingRepo = bookingRepo;
        this.courtRepo = courtRepo;
        this.customerRepo = customerRepo;
        this.converterClient = converterClient;
        logger.debug("BookingService initialized");
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(Long id) {
        logger.info("Tar bort bokning med id: {}", id);
        try {
            if (id == null) {
                logger.error("Id är null");
                throw new IllegalArgumentException("Id är null");
            }
            Booking booking = bookingRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Bokning med id {} hittades inte", id);
                        return new EntityNotFoundException("Bokning med id " + id + " hittades inte");
                    });
            bookingRepo.delete(booking);
            logger.info("Lyckades ta bort bokning med id: {}", id);
        } catch (Exception e) {
            logger.error("Error vid borttag av bokning med id: {}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> getAllBookings() {
        logger.info("Hämtar alla bokningar");
        try {
            List<BookingDto> bookings = bookingRepo.findAll().stream()
                    .map(this::toDtoWithEURPrice)
                    .toList();
            logger.debug("Lyckades hämta {} bokningar", bookings.size());
            return bookings;
        } catch (Exception e) {
            logger.error("Error vid hämtning av alla bokningar", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public BookingDto getBooking(Long id) {
        logger.info("Hämtar bokning med id: {}", id);
        try {
            if (id == null) {
                logger.error("Id är null");
                throw new IllegalArgumentException("Id är null");
            }
            Booking booking = bookingRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Bokning med id {} hittades inte", id);
                        return new EntityNotFoundException("Bokning med id " + id + " hittades inte");
                    });
            logger.debug("Lyckades hämta bokning med id: {}", id);
            return toDtoWithEURPrice(booking);
        } catch (Exception e) {
            logger.error("Error vid hämtning av bokning med id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public BookingDto createBooking(BookingCreateDto dto) {
        logger.info("Skapar ny bokning för customerId: {}, courtId: {}, date: {}, timeSlot: {}",
                dto.customerId(), dto.courtId(), dto.date(), dto.timeSlot());
        try {
            if (dto == null) {
                logger.error("Body är null");
                throw new IllegalArgumentException("Body är null");
            }
            if (dto.customerId() == null || dto.courtId() == null || dto.numberOfPlayers() == 0 ||
                    dto.date() == null || dto.timeSlot() == 0) {
                logger.error("Error: Inget fält får vara null eller 0");
                throw new IllegalArgumentException("Inget fält får vara null eller 0");
            }
            if (dto.numberOfPlayers() < 1 || dto.numberOfPlayers() > 4) {
                logger.error("Ogiltigt antal spelare: {}", dto.numberOfPlayers());
                throw new IllegalArgumentException("Antal spelare får inte vara lägre än 1 och inte högre än 4");
            }
            if (dto.timeSlot() < 9 || dto.timeSlot() > 21) {
                logger.error("Ogiltig starttid: {}:00", dto.timeSlot());
                throw new IllegalArgumentException("Bokningsbara timmar är mellan kl 9 - 21");
            }

            customerRepo.findById(dto.customerId())
                    .orElseThrow(() -> {
                        logger.error("Kund med id {} hittades inte", dto.customerId());
                        return new EntityNotFoundException("Kund med id " + dto.customerId() + " hittades inte");
                    });

            Court court = courtRepo.findById(dto.courtId())
                    .orElseThrow(() -> {
                        logger.error("Padelbana med id {} hittades inte", dto.courtId());
                        return new EntityNotFoundException("Padelbana med id " + dto.courtId() + " hittades inte");
                    });

            boolean isBooked = bookingRepo.findAll().stream()
                    .anyMatch(booking -> booking.getCourtId().equals(dto.courtId())
                            && booking.getDate().equals(dto.date())
                            && booking.getTimeSlot() == dto.timeSlot());
            if (isBooked) {
                logger.error("Starttid {}:00 datum {} för padelbana {} är redan bokad", dto.timeSlot(), dto.date(), dto.courtId());
                throw new IllegalStateException("Starttid Kl: " + dto.timeSlot() + ":00 datum " + dto.date() + " för padelbana " + dto.courtId() + " är redan bokad");
            }

            Booking booking = new Booking(dto.customerId(), dto.courtId(), dto.numberOfPlayers(), dto.date(), dto.timeSlot());
            booking.setTotalPrice(court.getPrice());
            Booking savedBooking = bookingRepo.save(booking);
            logger.info("Lyckades skapa bokning med id: {}", savedBooking.getId());
            return toDtoWithEURPrice(savedBooking);
        } catch (Exception e) {
            logger.error("Error vid skapande av bokning", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public List<AvailableDto> availableTimeSlots(LocalDate date, Long courtId) {
        logger.info("Hämtar lediga starttider för padelbana med id: {} datum: {}", courtId, date);
        try {
            if (date == null || courtId == null) {
                logger.error("Datum eller id är null");
                throw new IllegalArgumentException("Datum eller id är null");
            }
            if (date.isBefore(LocalDate.now())) {
                logger.error("Datum {} är före dagens datum", date);
                throw new IllegalArgumentException("Datum kan inte vara före dagens datum");
            }
            Court court = courtRepo.findById(courtId)
                    .orElseThrow(() -> {
                        logger.error("Padelbana med id {} hittades inte", courtId);
                        return new EntityNotFoundException("Padelbana med id " + courtId + " hittades inte");
                    });
            List<Integer> bookedTimeSlots = bookingRepo.findBookedTimeSlotsByCourtIdAndDate(courtId, date);
            List<Integer> allTimeSlots = IntStream.rangeClosed(9, 21).boxed().toList();
            List<AvailableDto> availableSlots = allTimeSlots.stream()
                    .filter(timeSlot -> !bookedTimeSlots.contains(timeSlot))
                    .map(timeSlot -> {
                        double priceInEUR;
                        try {
                            priceInEUR = converterClient.convertToEUR(court.getPrice());
                        } catch (Exception e) {
                            logger.warn("Misslyckades att konvertera pris till EUR för padelbana med id: {}. Sätter pris till 0.0", court.getId(), e);
                            priceInEUR = 0.0;
                        }
                        return AvailableMapper.toDto(court, date, timeSlot, priceInEUR);
                    })
                    .toList();
            logger.debug("Hittade {} lediga starttider för courtId: {} datum: {}", availableSlots.size(), courtId, date);
            return availableSlots;
        } catch (Exception e) {
            logger.error("Error vid hämtning av lediga starttider för courtId: {} datum: {}", courtId, date, e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public BookingDto patchBooking(Long id, BookingUpdateDto dto) {
        logger.info("Patch bokning med id: {}", id);
        try {
            if (id == null || dto == null) {
                logger.error("Id eller body är null");
                throw new IllegalArgumentException("Id eller body är null");
            }
            Booking bookingToUpdate = bookingRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Bokning med id {} hittades inte", id);
                        return new EntityNotFoundException("Bokning med id " + id + " hittades inte");
                    });

            boolean needsAvailabilityCheck = false;

            Long tempCourtId = bookingToUpdate.getCourtId();
            Long targetCourtId = bookingToUpdate.getCourtId();
            LocalDate targetDate = bookingToUpdate.getDate();
            int targetTimeSlot = bookingToUpdate.getTimeSlot();

            Court court = courtRepo.findById(tempCourtId)
                    .orElseThrow(() -> {
                        logger.error("Padelbana med id {} hittades inte", tempCourtId);
                        return new EntityNotFoundException("Padelbana med id " + tempCourtId + " hittades inte");
                    });

            if (dto.courtId() != null && !dto.courtId().equals(bookingToUpdate.getCourtId())) {
                Court updatedCourt = courtRepo.findById(dto.courtId())
                        .orElseThrow(() -> {
                            logger.error("Padelbana med id {} hittades inte", dto.courtId());
                            return new EntityNotFoundException("Padelbana med id " + dto.courtId() + " hittades inte");
                        });
                bookingToUpdate.setCourtId(dto.courtId());
                targetCourtId = dto.courtId();
                needsAvailabilityCheck = true;
                court = updatedCourt;
            }
            if (dto.date() != null && !dto.date().equals(bookingToUpdate.getDate())) {
                if (dto.date().isBefore(LocalDate.now())) {
                    logger.error("Bokningsdatum {} är före dagens datum", dto.date());
                    throw new IllegalArgumentException("Bokningsdatum kan inte vara före dagens datum");
                }
                bookingToUpdate.setDate(dto.date());
                targetDate = dto.date();
                needsAvailabilityCheck = true;
            }
            if (dto.timeSlot() != 0 && dto.timeSlot() != bookingToUpdate.getTimeSlot()) {
                if (dto.timeSlot() < 9 || dto.timeSlot() > 21) {
                    logger.error("Ogiltig starttid: {}:00", dto.timeSlot());
                    throw new IllegalArgumentException("Bokningsbara timmar är mellan kl 9 - 21");
                }
                if (targetDate.equals(LocalDate.now())) {
                    logger.error("Det går inte att göra ändringar i bokningar på dagens datum: {}", targetDate);
                    throw new IllegalArgumentException("Det går inte att göra ändringar i bokningar på dagens datum");
                }
                bookingToUpdate.setTimeSlot(dto.timeSlot());
                targetTimeSlot = dto.timeSlot();
                needsAvailabilityCheck = true;
            }
            if (dto.numberOfPlayers() != 0 && dto.numberOfPlayers() != bookingToUpdate.getNumberOfPlayers()) {
                if (dto.numberOfPlayers() < 1 || dto.numberOfPlayers() > 4) {
                    logger.error("Ogiltigt antal spelare: {}", dto.numberOfPlayers());
                    throw new IllegalArgumentException("Antal spelare måste vara mellan 1 och 4");
                }
                bookingToUpdate.setNumberOfPlayers(dto.numberOfPlayers());
            }
            if (needsAvailabilityCheck) {
                Optional<Booking> conflictingBooking = bookingRepo.findByCourtIdAndDateAndTimeSlot(targetCourtId, targetDate, targetTimeSlot);
                if (conflictingBooking.isPresent() && !conflictingBooking.get().getId().equals(id)) {
                    logger.error("Starttid {}:00 datum {} för padelbana {} är redan bokad", targetTimeSlot, targetDate, targetCourtId);
                    throw new IllegalStateException("Starttid kl: " + targetTimeSlot + ":00 datum " + targetDate + " för padelbana " + targetCourtId + " är redan bokad");
                }
            }
            if (court != null) {
                bookingToUpdate.setTotalPrice(court.getPrice());
            }
            Booking savedBooking = bookingRepo.save(bookingToUpdate);
            logger.info("Lyckades patcha bokning med id: {}", id);
            return toDtoWithEURPrice(savedBooking);
        } catch (Exception e) {
            logger.error("Error vid patchning av bokning med id: {}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public List<BookingDto> getBookingsByCustomerId(Long customerId) {
        logger.info("Hämtar bokningar för kund med id: {}", customerId);
        try {
            if (customerId == null) {
                logger.error("Id är null");
                throw new IllegalArgumentException("Id är null");
            }
            Customer customer = customerRepo.findById(customerId)
                    .orElseThrow(() -> {
                        logger.error("Kund med id {} hittades inte", customerId);
                        return new EntityNotFoundException("Kund med id " + customerId + " hittades inte");
                    });
            List<BookingDto> bookings = bookingRepo.findAll().stream()
                    .map(this::toDtoWithEURPrice)
                    .filter(booking -> booking.customerId().equals(customerId))
                    .toList();
            logger.debug("Lyckades hämta {} bokningar för kund med id: {}", bookings.size(), customerId);
            return bookings;
        } catch (Exception e) {
            logger.error("Error vid hämtning av bokningar för kund med id: {}", customerId, e);
            throw e;
        }
    }

    private BookingDto toDtoWithEURPrice(Booking booking) {
        double priceInEUR;
        try {
            priceInEUR = converterClient.convertToEUR(booking.getTotalPrice());
        } catch (Exception e) {
            logger.warn("Misslyckades att konvertera pris till EUR för bokning med id: {}. Sätter pris till 0.0", booking.getId(), e);
            priceInEUR = 0.0;
        }
        return BookingMapper.toDto(booking, priceInEUR);
    }

    private AvailableDto toDtoWithEURPrice(Court court, LocalDate date, int timeSlot) {
        double priceInEUR;
        try {
            priceInEUR = converterClient.convertToEUR(court.getPrice());
        } catch (Exception e) {
            logger.warn("Misslyckades att konvertera pris till EUR för padelbana med id: {}. Sätter pris till 0.0", court.getId(), e);
            priceInEUR = 0.0;
        }
        return AvailableMapper.toDto(court, date, timeSlot, priceInEUR);
    }

}
