package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Ticket;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;
    private final CustomerService customerService;

    public TicketService(TicketRepository ticketRepository, CustomerService customerService) {
        this.ticketRepository = ticketRepository;
        this.customerService = customerService;
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

}
