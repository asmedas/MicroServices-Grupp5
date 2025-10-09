package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    int countByScreeningId(Long screeningId);
}
