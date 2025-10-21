package com.sebbe.cinema;

import com.sebbe.cinema.dtos.addressDtos.CreateAddressDto;
import com.sebbe.cinema.dtos.customerDtos.CreateCustomerWithAccountDto;
import com.sebbe.cinema.entities.*;
import com.sebbe.cinema.enums.TechnicalEquipment;
import com.sebbe.cinema.enums.Type;
import com.sebbe.cinema.repositories.*;
import com.sebbe.cinema.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class CinemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaApplication.class, args);
	}

    @Bean
    public CommandLineRunner loadData(AddressService addressService, CustomerRepository customerRepository, FilmRepository filmRepository
    , CinemaHallRepository cinemaHallRepository, ScreeningRepository screeningRepository, TicketRepository ticketRepository
    , BookingService bookingService, TicketService ticketService, BookingRepository bookingRepository, AddressRepository addressRepository,
                                      CustomerService customerService, ScreeningService screeningService,
                                      KeycloakUserServiceImpl keycloakUserServiceImpl)
    {
        return args -> {


            Authentication adminAuth =
                    new TestingAuthenticationToken("system", null, List.of(
                            new SimpleGrantedAuthority("ROLE_ADMIN"),
                            new SimpleGrantedAuthority("ROLE_USER")
                    ));
            SecurityContextHolder.getContext().setAuthentication(adminAuth);
            if(customerRepository.count() > 0 && addressRepository.count() > 0 && screeningRepository.count() > 0 && ticketRepository.count() > 0
             && bookingRepository.count() > 0 && filmRepository.count() > 0){
                System.out.println("Database already contains data");
                return;
            }
            keycloakUserServiceImpl.initializeUsersOnStartup();


            // 5 filmer
            Film film = new Film( "The Shawshank Redemption", "Drama", 200);
            filmRepository.save(film);
            Film film2 = new Film(18, "Mission Impossible", "Action", 130);
            filmRepository.save(film2);
            filmRepository.save(new Film(18, "Pulp Fiction", "Drama", 200));
            filmRepository.save(new Film( "Shrek", "Comedy", 180));
            filmRepository.save(new Film(18, "Godfather 2", "Drama", 200));

            // 3 lokaler
            CinemaHall cinemaHall = new CinemaHall("Theater 1", 100);
            cinemaHallRepository.save(cinemaHall);
            CinemaHall cinemaHall2 = new CinemaHall("Theater 2", 100);
            cinemaHallRepository.save(cinemaHall2);
            cinemaHallRepository.save(new CinemaHall("Theater 3", 100));


            // 3 visningar
            Screening screening = new Screening(LocalDate.of(2025,10,8), film, cinemaHall, List.of(Type.FILM));
            screening.setPriceSek(screeningService.calculatePriceSek(cinemaHall));
            screening.setPriceUsd(screeningService.calculatePriceUsd(screening.getPriceSek()));
            screeningRepository.save(screening);
            Screening screening1 = new Screening(LocalDate.of(2023,10,8), film2, cinemaHall2, List.of(Type.FILM));
            screening1.setPriceSek(screeningService.calculatePriceSek(cinemaHall2));
            screening1.setPriceUsd(screeningService.calculatePriceUsd(screening1.getPriceSek()));
            screeningRepository.save(screening1);
            Screening screening2 = new Screening(
                    LocalDate.of(2023,10,8), "Tomas Wigell", cinemaHall2, List.of(Type.SPEAKER));
            screening2.setPriceSek(screeningService.calculatePriceSek(cinemaHall2));
            screening2.setPriceUsd(screeningService.calculatePriceUsd(screening2.getPriceSek()));
            screeningRepository.save(screening2);


            /**
             * 5 kunder
             * skapar min keycloak från cinema_realm.json med
             * förinställda inställningar och två användare, admin och user som används nedan
             * admin - direkt från keycloak
             */
            Customer customer1 = new Customer("7c52dbe1-1e8a-4d9a-8f92-fc07e1a3c417",
                    "admin", "admin", "admin@hotmail.com", 30);
            Address address = addressService
                    .findOrCreateAddress(new CreateAddressDto("hårdvallsgatan 18", "sundsvall", "85353"));
            customer1.addAddress(address);
            customerRepository.save(customer1);

            // user - direkt från keycloak
            Customer customer2 = new Customer("e3b0a6d4-5d9b-4a3f-8f07-91c3a6de7f45",
                    "user", "user", "user@hotmail.com", 27);
            customer2.addAddress(address);
            customerRepository.save(customer2);

            customerService.createCustomerWithKeycloakUserAndAddress(new CreateCustomerWithAccountDto(
                    "Gunnar", "Jonsson", 50, "gunnar", "jonsson",
                    "Gunnar@hotmail.com", new CreateAddressDto("Russvägen 18", "sundsvall", "85752")));
            customerService.createCustomerWithKeycloakUserAndAddress(new CreateCustomerWithAccountDto(
                    "Ingrid", "Nordström", 50, "ingrid", "nordstrom",
                    "Ingrid@hotmail.com", new CreateAddressDto("Kolstavägen 37", "sundsvall", "65233")));
            customerService.createCustomerWithKeycloakUserAndAddress(new CreateCustomerWithAccountDto(
                    "Tomas", "Wigell", 50, "gabbi", "gabbi",
                    "wigell@hotmail.com", new CreateAddressDto("högomsvägen 13", "sundsvall", "54311")));


            // 2 bokningar och 1 ticket
            Ticket ticket = ticketService.commandLineRunner(screening2, customer1);
            customer1.setTickets(List.of(ticket));
            Booking booking = bookingService.commandLineRunnerFilm(cinemaHall, customer1, film, LocalDate.now());
            customer1.setBookings(List.of(booking));
            Booking booking2 = bookingService.commandLineRunnerSpeaker(cinemaHall2, customer2, "sebbes föreläsning", LocalDate.of(2026, 4, 10));
            customer2.setBookings(List.of(booking2));
        };
    }

}
