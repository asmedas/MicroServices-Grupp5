package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Screening;
import com.sebbe.cinema.repositories.ScreeningRepository;
import com.sebbe.cinema.repositories.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final TicketRepository ticketRepository;

    public ScreeningService(ScreeningRepository screeningRepository, TicketRepository ticketRepository) {
        this.screeningRepository = screeningRepository;
        this.ticketRepository = ticketRepository;
    }

    public int getRemainingSeats(Screening screening) {
        if (screening.getBooking() != null) {
            return 0;
        }
        int hallCapacity = screening.getCinemaHall().getMaxSeats();
        int soldTickets = ticketRepository.countByScreeningId(screening.getId());
        return hallCapacity - soldTickets;
    }


}
