package com.sebbe.cinema;

import com.sebbe.cinema.entities.*;
import com.sebbe.cinema.enums.TechnicalEquipment;
import com.sebbe.cinema.enums.Type;
import com.sebbe.cinema.repositories.*;
import com.sebbe.cinema.services.AddressService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class CinemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaApplication.class, args);
	}

    @Bean
    public CommandLineRunner loadData(AddressService addressService, CustomerRepository customerRepository, FilmRepository filmRepository
    , CinemaHallRepository cinemaHallRepository, ScreeningRepository screeningRepository, TicketRepository ticketRepository
    , BookingRepository bookingRepository){
        return args -> {
            Address address1 = addressService.findOrCreateAddress("Kungsgatan 12", "Stockholm", "111 11");

            Film film = new Film(18, "The Shawshank Redemption", "Drama");
            filmRepository.save(film);

            CinemaHall cinemaHall = new CinemaHall("Theater 1", 100, List.of(TechnicalEquipment.PROJECTOR,TechnicalEquipment.MIC), null);
            cinemaHallRepository.save(cinemaHall);

            Screening screening = new Screening(BigDecimal.valueOf(5000), LocalDate.of(2025,10,8), List.of(Type.FILM), film, cinemaHall);
            screeningRepository.save(screening);

            Customer customer1 = new Customer("cb5fbcfc-4be0-42b2-bc63-6149bfde6106","sebbe", 20, Set.of(address1));
            customerRepository.save(customer1);


            Ticket ticket = new Ticket(screening, BigDecimal.valueOf(75.09), BigDecimal.valueOf(25));
            ticket.setCustomer(customer1);
            customer1.setTickets(List.of(ticket));

            customerRepository.save(customer1);
        };
    }

}
