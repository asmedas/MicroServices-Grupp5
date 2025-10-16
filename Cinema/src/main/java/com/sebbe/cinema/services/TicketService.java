package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Customer;
import com.sebbe.cinema.entities.Screening;
import com.sebbe.cinema.entities.Ticket;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.repositories.CustomerRepository;
import com.sebbe.cinema.repositories.ScreeningRepository;
import com.sebbe.cinema.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final ScreeningRepository screeningRepository;
    private final CallCurrencyApiService callCurrencyApiService;

    public TicketService(TicketRepository ticketRepository, CustomerRepository customerRepository,
                         CallCurrencyApiService callCurrencyApiService, ScreeningRepository screeningRepository) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.callCurrencyApiService = callCurrencyApiService;
        this.screeningRepository = screeningRepository;
    }

    @PreAuthorize("hasRole('USER')")
    public Ticket createTicket(Long screeningId) {
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NoMatchException("Customer not found for current user"));
        log.debug("Booking a ticket");
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> {
                    log.error("Screening with id {} not found", screeningId);
                    return new NoMatchException("Screening not found");
                });
        if (getRemainingSeats(screening) <= 0) {
            throw new IllegalStateException("Not enough seats available");
        }
        Ticket ticket = new Ticket(screening, customer);
        ticket.setPriceSek(calculatePriceSek(screening));
        ticket.setPriceUsd(calculatePriceUsd(ticket.getPriceSek()));
        return ticketRepository.save(ticket);
    }

    @PreAuthorize("hasRole('USER')")
    public List<Ticket> getTicketsByCustomerId(Long customerid){
        log.debug("Fetching tickets for current user");
        return ticketRepository.findByCustomerId(customerid);
    }

    public void deleteTicket(Long id){
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ticket with id {} not found", id);
                    return new NoMatchException("Ticket not found");
                });
        ticket.removeTicketFromConnections();
        try{
            ticketRepository.delete(ticket);
        } catch (DataAccessException e){
            log.error("Error deleting ticket", e);
            throw new UnexpectedError("Error deleting ticket " + e);
        }
    }

    private BigDecimal calculatePriceSek(Screening screening){
        int seats = screening.getCinemaHall().getMaxSeats();
        BigDecimal totalPrice = screening.getCinemaHall().getPrice();
        return totalPrice.divide(BigDecimal.valueOf(seats), RoundingMode.UNNECESSARY);
    }

    private BigDecimal calculatePriceUsd(BigDecimal priceSek){
        return callCurrencyApiService.convertFromSEKToUsd(priceSek);
    }

    public Ticket commandLineRunner(Screening screening, Customer customer){
        Ticket ticket = new Ticket(screening, customer);
        ticket.setPriceSek(calculatePriceSek(screening));
        ticket.setPriceUsd(calculatePriceUsd(ticket.getPriceSek()));
        return ticketRepository.save(ticket);
    }

    private int getRemainingSeats(Screening screening) {
        log.debug("Calculating remaining seats for screening with id {}", screening.getId());
        int hallCapacity = screening.getCinemaHall().getMaxSeats();
        int soldTickets = ticketRepository.countByScreeningId(screening.getId());
        return hallCapacity - soldTickets;
    }

}
