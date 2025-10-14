package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Booking;
import com.sebbe.cinema.entities.Screening;
import com.sebbe.cinema.entities.Ticket;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.repositories.ScreeningRepository;
import com.sebbe.cinema.repositories.TicketRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ScreeningService {

    private static final Logger log = LoggerFactory.getLogger(ScreeningService.class);
    private final BookingService bookingService;
    private final TicketService ticketService;
    private final ScreeningRepository screeningRepository;
    private final TicketRepository ticketRepository;

    public ScreeningService(ScreeningRepository screeningRepository, TicketRepository ticketRepository,
                            BookingService bookingService, TicketService ticketService) {
        this.screeningRepository = screeningRepository;
        this.ticketRepository = ticketRepository;
        this.bookingService = bookingService;
        this.ticketService = ticketService;
    }

    public void deleteScreening(Long id){
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Screening with id {} not found", id);
                    return new NoMatchException("Screening not found");
                });
        try{
            screening.removeScreeningFromConnections();
            if(screening.getBooking() != null){
                bookingService.deleteBooking(screening.getBooking().getId());
            }
            if(!screening.getTickets().isEmpty()){
                for(Ticket ticket: List.copyOf(screening.getTickets())){
                    ticketService.deleteTicket(ticket.getId());
                }
            }
            screeningRepository.delete(screening);
        } catch (DataAccessException e){
            log.error("Error deleting screening", e);
            throw new UnexpectedError("Error deleting screening " + e);
        }
    }

    public int getRemainingSeats(Screening screening) {
        log.debug("Calculating remaining seats for screening with id {}", screening.getId());
        if (screening.getBooking() != null) {
            return 0;
        }
        int hallCapacity = screening.getCinemaHall().getMaxSeats();
        int soldTickets = ticketRepository.countByScreeningId(screening.getId());
        return hallCapacity - soldTickets;
    }


}
