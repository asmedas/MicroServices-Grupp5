package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.BookingRequestDto;
import com.zetterlund.wigell_sushi_api.entity.Booking;
import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.entity.Room;
import com.zetterlund.wigell_sushi_api.repository.BookingRepository;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.repository.CustomerRepository;
import com.zetterlund.wigell_sushi_api.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository,  CustomerRepository customerRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    public List<Booking> getBookingsByCustomerId(Integer customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public Booking addBooking(BookingRequestDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setDate(dto.getDate());
        booking.setGuestCount(dto.getGuestCount());
        booking.setCatering(dto.getCatering());
        booking.setTechnicalEquipment(dto.getTechnicalEquipment());

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Integer id, Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existingBooking.setDate(updatedBooking.getDate());
        existingBooking.setGuestCount(updatedBooking.getGuestCount());
        existingBooking.setRoom(updatedBooking.getRoom());
        existingBooking.setBookingDetails(updatedBooking.getBookingDetails());
        existingBooking.setCustomer(updatedBooking.getCustomer());

        return bookingRepository.save(existingBooking);
    }

    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));
    }
}
