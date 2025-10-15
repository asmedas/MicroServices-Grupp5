package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.AvailableDto;
import com.strom.wigellPadel.dto.BookingCreateDto;
import com.strom.wigellPadel.dto.BookingDto;
import com.strom.wigellPadel.dto.BookingUpdateDto;
import com.strom.wigellPadel.entities.Booking;
import com.strom.wigellPadel.entities.Court;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.mapper.BookingMapper;
import com.strom.wigellPadel.mapper.CustomerMapper;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CourtRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
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

    private final BookingRepo bookingRepo;
    private final CourtRepo courtRepo;
    private final CustomerRepo customerRepo;

    public BookingService(BookingRepo bookingRepo, CourtRepo courtRepo, CustomerRepo customerRepo) {
        this.bookingRepo = bookingRepo;
        this.courtRepo = courtRepo;
        this.customerRepo = customerRepo;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id är null");
        }
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + id + " hittades inte"));
        bookingRepo.delete(booking);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll().stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public BookingDto getBooking(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id är null");
        }
        return bookingRepo.findById(id)
                .map(BookingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + id + " hittades inte"));
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public BookingDto createBooking(BookingCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Body är null");
        }

        if (dto.customerId() == null ||
            dto.courtId() == null ||
            dto.numberOfPlayers() == 0 ||
            dto.date() == null ||
            dto.timeSlot() == 0) {
            throw new IllegalArgumentException("Inget fält får vara null eller 0");
        }

        if (dto.numberOfPlayers() < 1 || dto.numberOfPlayers() > 4) {
            throw new IllegalArgumentException("Antal spelare får inte vara lägre än 1 och inte högre än 4");
        }

        if (dto.timeSlot() < 9 || dto.timeSlot() > 21) {
            throw new IllegalArgumentException("Bokningsbara timmar är mellan kl 9 - 21");
        }

        customerRepo.findById(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + dto.customerId() + " hittades inte"));

        Court court = courtRepo.findById(dto.courtId())
                .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + dto.courtId() + " hittades inte"));

        boolean isBooked = bookingRepo.findAll().stream()
                .anyMatch(booking -> booking.getCourtId().equals(dto.courtId())
                        && booking.getDate().equals(dto.date())
                        && booking.getTimeSlot() == dto.timeSlot());
        if (isBooked) {
            throw new IllegalStateException("Tid Kl: " + dto.timeSlot() + ":00 datum " + dto.date() + " för padelbana " + dto.courtId() + " är redan bokad");
        }

        Booking booking = new Booking(
                dto.customerId(),
                dto.courtId(),
                dto.numberOfPlayers(),
                dto.date(),
                dto.timeSlot()
        );

        booking.setTotalPrice(court.getPrice());

        Booking savedBooking = bookingRepo.save(booking);

        return BookingMapper.toDto(savedBooking);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public List<AvailableDto> availableTimeSlots(LocalDate date, Long courtId) {
        if (date == null || courtId == null) {
            throw new IllegalArgumentException("Datum eller id är null");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Datum kan inte vara före dagens datum");
        }
        Court court = courtRepo.findById(courtId)
                .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + courtId + " hittades inte"));
        List<Integer> bookedTimeSlots = bookingRepo.findBookedTimeSlotsByCourtIdAndDate(courtId, date);
        List<Integer> allTimeSlots = IntStream.rangeClosed(9, 21)
                .boxed()
                .toList();
        List<AvailableDto> availableSlots = allTimeSlots.stream()
                .filter(timeSlot -> !bookedTimeSlots.contains(timeSlot))
                .map(timeSlot -> new AvailableDto(
                        courtId,
                        date,
                        timeSlot,
                        court.getPrice()
                ))
                .toList();
        return availableSlots;
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public BookingDto patchBooking(Long id, BookingUpdateDto dto) {
        if (id == null || dto == null) {
            throw new IllegalArgumentException("Id eller body är null");
        }
        Booking bookingToUpdate = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + id + " hittades inte"));

        boolean needsAvailabilityCheck = false;

        Long targetCourtId = bookingToUpdate.getCourtId();
        LocalDate targetDate = bookingToUpdate.getDate();
        int targetTimeSlot = bookingToUpdate.getTimeSlot();

        Court court = courtRepo.findById(targetCourtId)
                .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + dto.courtId() + " hittades inte"));

        if (dto.courtId() != null && !dto.courtId().equals(bookingToUpdate.getCourtId())) {
            court = courtRepo.findById(dto.courtId())
                    .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + dto.courtId() + " hittades inte"));
            bookingToUpdate.setCourtId(dto.courtId());
            targetCourtId = dto.courtId();
            needsAvailabilityCheck = true;
        }
        if (dto.date() != null && !dto.date().equals(bookingToUpdate.getDate())) {
            if (dto.date().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Bokningsdatum kan inte vara före dagens datum");
            }
            bookingToUpdate.setDate(dto.date());
            targetDate = dto.date();
            needsAvailabilityCheck = true;
        }
        if (dto.timeSlot() != 0 && dto.timeSlot() != bookingToUpdate.getTimeSlot()) {
            if (dto.timeSlot() < 9 || dto.timeSlot() > 21) {
                throw new IllegalArgumentException("Bokningsbara timmar är mellan kl 9 - 21");
            }
            if (targetDate.equals(LocalDate.now())) {
                throw new IllegalArgumentException("Det går inte att göra ändringar i bokningar på dagens datum");
            }
            bookingToUpdate.setTimeSlot(dto.timeSlot());
            targetTimeSlot = dto.timeSlot();
            needsAvailabilityCheck = true;
        }
        if (dto.numberOfPlayers() != 0 && dto.numberOfPlayers() != bookingToUpdate.getNumberOfPlayers()) {
            if (dto.numberOfPlayers() < 1 || dto.numberOfPlayers() > 4) {
                throw new IllegalArgumentException("Antal spelare måste vara mellan 1 och 4");
            }
            bookingToUpdate.setNumberOfPlayers(dto.numberOfPlayers());
        }
        if (needsAvailabilityCheck) {
            Optional<Booking> conflictingBooking = bookingRepo.findByCourtIdAndDateAndTimeSlot(targetCourtId, targetDate, targetTimeSlot);
            if (conflictingBooking.isPresent() && !conflictingBooking.get().getId().equals(id)) {
                throw new IllegalStateException("Tid kl: " + targetTimeSlot + ":00 datum " + targetDate + " för padelbana " + targetCourtId + " är redan bokad");
            }
        }
        if (court != null) {
            bookingToUpdate.setTotalPrice(court.getPrice());
        }
        Booking savedBooking = bookingRepo.save(bookingToUpdate);
        return BookingMapper.toDto(savedBooking);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public List<BookingDto> getBookingsByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Id är null");
        }

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + customerId + " hittades inte"));

        return bookingRepo.findAll().stream()
                .map(BookingMapper::toDto)
                .filter(booking -> booking.customerId().equals(customerId))
                .toList();
    }

}
