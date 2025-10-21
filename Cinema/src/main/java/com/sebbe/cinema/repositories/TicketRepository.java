package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.Customer;
import com.sebbe.cinema.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    int countByScreeningId(Long screeningId);

    void deleteAllByCustomerId(Long customerId);

    List<Ticket> findByCustomerId(Long customerId);
}
