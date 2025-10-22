package com.strom.wigellPadel.config;

import com.strom.wigellPadel.entities.Address;
import com.strom.wigellPadel.entities.Booking;
import com.strom.wigellPadel.entities.Court;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.repositories.AddressRepo;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CourtRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import com.strom.wigellPadel.services.CustomerService;
import com.strom.wigellPadel.dto.CustomerCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Transactional
@Configuration
public class DataInit {

    private static final Logger logger = LoggerFactory.getLogger(DataInit.class);

    private final CustomerRepo customerRepo;
    private final AddressRepo addressRepo;
    private final CourtRepo courtRepo;
    private final BookingRepo bookingRepo;
    private final CustomerService customerService;

    public DataInit(CustomerRepo customerRepo, AddressRepo addressRepo, CourtRepo courtRepo, BookingRepo bookingRepo, CustomerService customerService) {
        this.customerRepo = customerRepo;
        this.addressRepo = addressRepo;
        this.courtRepo = courtRepo;
        this.bookingRepo = bookingRepo;
        this.customerService = customerService;
        logger.debug("DataInit initialized");
    }

    @Bean(name = "initialDataLoader")
    CommandLineRunner dataInitializer() {
        return args -> {
            Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
            try {

                Authentication adminAuth = new TestingAuthenticationToken("system", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                SecurityContextHolder.getContext().setAuthentication(adminAuth);

                List<Customer> createdCustomers = new ArrayList<>();
                if (customerRepo.count() == 0) {
                    logger.info("Inga kunder hittades, skapar 5 kunder");

                    CustomerCreateDto dto1 = new CustomerCreateDto("Robert", "De Niro", "testgatan 1", "12345", "Stockholm", "robert.deniro@mail.com", "robert", "robert");
                    customerService.createCustomer(dto1);
                    Customer customer1 = customerRepo.findByUsername("robert").orElseThrow(() -> new RuntimeException("Misslyckades att skapa kund: robert"));
                    createdCustomers.add(customer1);
                    logger.debug("Skapat kund: {}", customer1.getUsername());

                    CustomerCreateDto dto2 = new CustomerCreateDto("Johnny", "Depp", "testgatan 2", "23456", "Göteborg", "johnny.depp@mail.com", "johnny", "johnny");
                    customerService.createCustomer(dto2);
                    Customer customer2 = customerRepo.findByUsername("johnny").orElseThrow(() -> new RuntimeException("Misslyckades att skapa kund: johnny"));
                    createdCustomers.add(customer2);
                    logger.debug("Skapat kund: {}", customer2.getUsername());

                    CustomerCreateDto dto3 = new CustomerCreateDto("Christian", "Bale", "testgatan 3", "34567", "Malmö", "christian.bale@mail.com", "christian", "christian");
                    customerService.createCustomer(dto3);
                    Customer customer3 = customerRepo.findByUsername("christian").orElseThrow(() -> new RuntimeException("Misslyckades att skapa kund: christian"));
                    createdCustomers.add(customer3);
                    logger.debug("Skapat kund: {}", customer3.getUsername());

                    CustomerCreateDto dto4 = new CustomerCreateDto("Joaquin", "Phoenix", "testgatan 4", "45678", "Uppsala", "joaquin.phoenix@mail.com", "joaquin", "joaquin");
                    customerService.createCustomer(dto4);
                    Customer customer4 = customerRepo.findByUsername("joaquin").orElseThrow(() -> new RuntimeException("Misslyckades att skapa kund: joaquin"));
                    createdCustomers.add(customer4);
                    logger.debug("Skapat kund: {}", customer4.getUsername());

                    CustomerCreateDto dto5 = new CustomerCreateDto("Quentin", "Tarantino", "testgatan 5", "56789", "Sundsvall", "quentin.tarantino@mail.com", "quentin", "quentin");
                    customerService.createCustomer(dto5);
                    Customer customer5 = customerRepo.findByUsername("quentin").orElseThrow(() -> new RuntimeException("Misslyckades att skapa kund: quentin"));
                    createdCustomers.add(customer5);
                    logger.debug("Skapat kund: {}", customer5.getUsername());
                } else {
                    logger.info("Det finns redan kunder i databasen, hoppar över kundskapandet");
                }

                List<Court> createdCourts = new ArrayList<>();
                if (courtRepo.count() == 0) {
                    logger.info("Inga padelbanor hittades, skapar 5 padelbanor");
                    createdCourts.add(courtRepo.save(new Court("Inomhusbana A", 250.0)));
                    createdCourts.add(courtRepo.save(new Court("Utomhusbana B", 200.0)));
                    createdCourts.add(courtRepo.save(new Court("Inomhusbana C", 250.0)));
                    createdCourts.add(courtRepo.save(new Court("Utomhusbana D", 200.0)));
                    createdCourts.add(courtRepo.save(new Court("Inomhusbana E", 250.0)));
                    logger.debug("Skapade {} padelbanor", createdCourts.size());
                } else {
                    logger.info("Padelbanor finns redan i databasen, hoppar över padelbaneskapandet");
                }

                if (bookingRepo.count() == 0 && !createdCustomers.isEmpty() && !createdCourts.isEmpty()) {
                    logger.info("Inga bokningar hittades, skapar 2 bokningar");

                    Long customerId1 = createdCustomers.get(0).getId();
                    Long courtId1 = createdCourts.get(0).getId();
                    double price1 = createdCourts.get(0).getPrice();
                    LocalDate date1 = LocalDate.of(2025, 11, 23);
                    int timeSlot1 = 10;
                    Booking booking1 = new Booking(customerId1, courtId1, 2, date1, timeSlot1, price1);
                    bookingRepo.save(booking1);
                    logger.debug("Bokning skapad för kund-ID: {}, padelbana-ID: {}, datum: {}, klockslag: {}", customerId1, courtId1, date1, timeSlot1);

                    Long customerId2 = createdCustomers.get(1).getId();
                    Long courtId2 = createdCourts.get(1).getId();
                    double price2 = createdCourts.get(1).getPrice();
                    LocalDate date2 = LocalDate.of(2025, 11, 24);
                    int timeSlot2 = 15;
                    Booking booking2 = new Booking(customerId2, courtId2, 4, date2, timeSlot2, price2);
                    bookingRepo.save(booking2);
                    logger.debug("Bokning skapad för kund-ID: {}, padelbana-ID: {}, datum: {}, klockslag: {}", customerId2, courtId2, date2, timeSlot2);
                } else {
                    logger.info("Bokningar finns redan eller inga kunder/padelbanor tillgängliga, hoppar över bokningsskapandet");
                }
            } finally {
                SecurityContextHolder.getContext().setAuthentication(originalAuth);
            }
        };
    }
}