package com.andreas.wigellmcrental.config;

import com.andreas.wigellmcrental.entity.*;
import com.andreas.wigellmcrental.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(BikeRepository bikes,
                           CustomerRepository customers,
                           BookingRepository bookings,
                           AddressRepository addresses) {
        return args -> {
            if (bikes.count() == 0) {
                bikes.save(new Bike("BMW", "R1250GS", 2023, 950, true));
                bikes.save(new Bike("Yamaha", "MT-07", 2022, 650, true));
                bikes.save(new Bike("Kawasaki", "Z900", 2023, 800, true));
                bikes.save(new Bike("Triumph", "Tiger 900", 2024, 1000, true));
                bikes.save(new Bike("Harley-Davidson", "Sportster S", 2023, 1200, true));
            }

            if (customers.count() == 0) {
                customers.save(new Customer("andreas", "Andreas Karlsson", "andreas@mail.com", "0701234567"));
                customers.save(new Customer("åke", "Åke Nilsson", "ake@mail.com", "0707654321"));
                customers.save(new Customer("dragan", "Dragan Andersson", "dragan@mail.com", "0703334444"));
                customers.save(new Customer("conny", "Conny Johansson", "conny@mail.com", "0709876543"));
                customers.save(new Customer("emil", "Emil Svensson", "emil@mail.com", "0705555555"));
            }

            if (bookings.count() == 0) {
                Customer c = customers.findAll().get(0);
                Bike b = bikes.findAll().get(0);

                Booking booking1 = new Booking();
                booking1.setCustomer(c);
                booking1.setBike(b);
                booking1.setStartDate(LocalDate.now());
                booking1.setEndDate(LocalDate.now().plusDays(3));
                booking1.setTotalPriceSek(950 * 3);
                booking1.setTotalPriceGbp(950 * 3 * 0.075);
                booking1.setStatus(BookingStatus.ACTIVE);
                bookings.save(booking1);

                Booking booking2 = new Booking();
                booking2.setCustomer(customers.findAll().get(1));
                booking2.setBike(bikes.findAll().get(1));
                booking2.setStartDate(LocalDate.now().plusDays(5));
                booking2.setEndDate(LocalDate.now().plusDays(8));
                booking2.setTotalPriceSek(650 * 3);
                booking2.setTotalPriceGbp(650 * 3 * 0.075);
                booking2.setStatus(BookingStatus.RETURNED);
                bookings.save(booking2);
            }

            // Lägg till adresser till kunder (om inga finns)
            if (addresses.count() == 0) {
                Customer c1 = customers.findAll().get(0);
                Customer c2 = customers.findAll().get(1);

                Address a1 = new Address("Stockholm", "Kungsgatan 1", "11111", "Sweden");
                a1.setCustomer(c1);
                addresses.save(a1);

                Address a2 = new Address("Göteborg", "Avenyn 5", "41141", "Sweden");
                a2.setCustomer(c2);
                addresses.save(a2);
            }

            System.out.println("Testdata laddad: 5 kunder, 5 hojar, 2 bokningar, 2 adresser");
        };
    }
}
