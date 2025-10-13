package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Screening;
import com.sebbe.cinema.repositories.ScreeningRepository;
import com.sebbe.cinema.repositories.TicketRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ScreeningService {

    private static final Logger log = LoggerFactory.getLogger(ScreeningService.class);
    private final ScreeningRepository screeningRepository;
    private final TicketRepository ticketRepository;

    public ScreeningService(ScreeningRepository screeningRepository, TicketRepository ticketRepository) {
        this.screeningRepository = screeningRepository;
        this.ticketRepository = ticketRepository;
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
