package com.andreas.wigellmcrental.repository;

import com.andreas.wigellmcrental.entity.Booking;
import com.andreas.wigellmcrental.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Hitta bokningar för en kund
    List<Booking> findByCustomer_Id(Long customerId);

    // Hitta bokningar för en viss motorcykel som överlappar ett datumintervall
    @Query("""
       select b from Booking b
       where b.bike.id = :bikeId
         and not (b.endDate <= :from or b.startDate >= :to)
       """)
    List<Booking> findOverlapping(@Param("bikeId") Long bikeId,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to);


    // Kontrollera om en hoj har en aktiv bokning
    boolean existsByBike_IdAndStatus(Long bikeId, BookingStatus status);
}
