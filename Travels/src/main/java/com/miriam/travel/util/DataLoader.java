package com.miriam.travel.util;

import com.miriam.travel.entity.Address;
import com.miriam.travel.entity.Customer;
import com.miriam.travel.entity.Destination;
import com.miriam.travel.repository.AddressRepository;
import com.miriam.travel.repository.CustomerRepository;
import com.miriam.travel.repository.DestinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final DestinationRepository destinationRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public DataLoader(DestinationRepository destinationRepository,
                      CustomerRepository customerRepository,
                      AddressRepository addressRepository) {
        this.destinationRepository = destinationRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedDestinations();
        seedCustomers();
    }


    private void seedDestinations() {
        if (destinationRepository.count() == 0) {
            destinationRepository.saveAll(List.of(
                    new Destination("Solrosen", "Malmö", "Sverige", new BigDecimal("7800")),
                    new Destination("Fjärilen", "Göteborg", "Spanien", new BigDecimal("6500")),
                    new Destination("Solen", "Kristianstad", "Danmark", new BigDecimal("10500")),
                    new Destination("Månen", "Berlin", "Tyskland", new BigDecimal("7200")),
                    new Destination("Pluto", "Krakow", "Polen", new BigDecimal("5400"))
            ));
            log.info("5 destinations seeded successfully.");
        } else {
            log.info("ℹ Destinations already exist, skipping seeding.");
        }
    }


    private void seedCustomers() { if (customerRepository.count() == 0) {
        String adminTokenId = "7c52dbe1-1e8a-4d9a-8f92-fc07e1a3c417";
        String userTokenId = "e3b0a6d4-5d9b-4a3f-8f07-91c3a6de7f45";

        Customer admin = new Customer(
                adminTokenId,
                "admin",
                "Admin",
                "admin@wigell.se",
                "ADMIN" );

        Customer user = new Customer(
                userTokenId,
                "user",
                "User",
                "user@wigell.se",
                "USER" );

        Customer robin = new Customer("robin", "robin", "Robin Svensson", "robin@svensson.se", "USER");
        Customer miriam = new Customer("miriam", "miriam", "Miriam Örn", "miriam@hotmail.se", "USER");
        Customer aleyah = new Customer("aleyah", "aleyah", "Aleyah Nilsson", "aleyah@nilsson.se", "USER");

        customerRepository.saveAll(List.of(admin, user, robin, miriam, aleyah));
        log.info("5 customers seeded successfully (including admin and user).");

        // Skapa adresser kopplade till kunder
        Address a1 = new Address("Betgatan 23",  "Simrishamn", "75001",  "Sverige", admin);
        Address a2 = new Address("Kungsgatan 4", "Stockholm",  "29001",  "Spanien", user);
        Address a3 = new Address("Ponnygatan 3", "Helsingborg","10134",  "Island",  robin);
        Address a4 = new Address("Slottsvägen 12","Luleå",     "11120",  "Sverige", miriam);
        Address a5 = new Address("Ryttargatan 10","Umeå",      "31021",  "Polen",   aleyah);

        addressRepository.saveAll(List.of(a1, a2, a3, a4, a5));
        log.info("5 addresses created");
    } else {
        log.info(" Customers already exist, skipping seeding.");
    }
    }
}
