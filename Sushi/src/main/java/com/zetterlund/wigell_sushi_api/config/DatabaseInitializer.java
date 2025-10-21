package com.zetterlund.wigell_sushi_api.config;

import com.zetterlund.wigell_sushi_api.repository.*;
import com.zetterlund.wigell_sushi_api.entity.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Configuration
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Bean
    public CommandLineRunner loadData(
            CustomerRepository custRepo,
            AddressRepository addrRepo,
            DishRepository dishRepo,
            RoomRepository roomRepo,
            OrderRepository orderRepo,
            OrderDetailRepository orderDetailRepo,
            BookingRepository bookingRepo,
            BookingDetailRepository bookingDetailRepo) {

        return args -> {
                log.info("=== Clearing all tables ===");
                bookingDetailRepo.deleteAll();
                bookingRepo.deleteAll();
                orderDetailRepo.deleteAll();
                orderRepo.deleteAll();
                addrRepo.deleteAll();
                custRepo.deleteAll();
                dishRepo.deleteAll();
                roomRepo.deleteAll();

                log.info("=== Seeding database ===");

                // 5 kunder
                Customer legolas = custRepo.save(new Customer(null, "Legolas", "Legolas", "Greenleaf",
                        "legolas@middleearth.com", "047888021"));
                Customer gimli  = custRepo.save(new Customer(null, "Gimli", "Gimli", "Son of Gloin",
                        "gimli@middleearth.com", "053999879"));
                Customer gandalf = custRepo.save(new Customer(null, "Gandalf", "Gandalf", "The Grey",
                        "gandalf@middleearth.com", "000111001"));
                Customer merry   = custRepo.save(new Customer(null, "Merry", "Meriadoc", "Brandybuck",
                        "merry@middleearth.com", "046222003"));
                Customer pippin  = custRepo.save(new Customer(null, "Pippin", "Peregrin", "Took",
                        "pippin@middleearth.com", "046222004"));

                // 5 adresser
                addrRepo.save(new Address("Royal Halls of Thranduil", "334 777", "Mirkwood", legolas));
                addrRepo.save(new Address("The Lonley Mountain", "444 222", "Erebor", gimli));
                addrRepo.save(new Address("Unknown", "999 999", "c/o The Roads of Middle-earth", gandalf));
                addrRepo.save(new Address("Brandy Hall, Buckland", "221 118", "The Shire", merry));
                addrRepo.save(new Address("Great Smials, Tuckborough", "220 555", "The Shire", pippin));

                // 5 rätter
                Dish sushi   = dishRepo.save(new Dish(null, "Sushi Combo", new BigDecimal("150.00"),
                        new BigDecimal("1200.00"), "10 bitar av olika sushibitar"));
                Dish ramen   = dishRepo.save(new Dish(null, "Ramen Bowl", new BigDecimal("90.00"),
                        new BigDecimal("720.00"), "Supergod ramen med fläsk och ägg"));
                Dish edamame = dishRepo.save(new Dish(null, "Edamame", new BigDecimal("40.00"),
                        new BigDecimal("320.00"), "Ångade soyaböner med havssalt"));
                Dish tempura = dishRepo.save(new Dish(null, "Tempura Prawns", new BigDecimal("110.00"),
                        new BigDecimal("880.00"), "Krispriga tempuraräkor"));
                Dish miso    = dishRepo.save(new Dish(null, "Miso Soup", new BigDecimal("35.00"),
                        new BigDecimal("280.00"), "Traditionell Japansk miso soppa"));

                // 3 lokaler
                Room mainHall   = roomRepo.save(new Room(null, "Main Hall", 50, true, new ArrayList<>()));
                Room privateR   = roomRepo.save(new Room(null, "Private Room", 10, false, new ArrayList<>()));
                Room vipLounge  = roomRepo.save(new Room(null, "VIP Lounge", 20, true,  new ArrayList<>()));

                // 2 beställningar och orderdetaljer
                Order order1 = orderRepo.save(new Order(null, new BigDecimal("280.00"), legolas));
                Order order2 = orderRepo.save(new Order(null, new BigDecimal("195.00"), gimli));

                orderDetailRepo.save(new OrderDetails(null, order1, sushi, 1));
                orderDetailRepo.save(new OrderDetails(null, order1, miso, 1));
                orderDetailRepo.save(new OrderDetails(null, order2, ramen, 2));
                orderDetailRepo.save(new OrderDetails(null, order2, tempura, 1));

                // 3 bokningar och bokningsdetaljer
                Booking book1 = bookingRepo.save(new Booking(null,
                        LocalDateTime.of(2025,10,8,14,0),
                        mainHall, gandalf, 20));
                Booking book2 = bookingRepo.save(new Booking(null,
                        LocalDateTime.of(2025,10,9,18,0),
                        privateR, merry, 6));
                Booking book3 = bookingRepo.save(new Booking(null,
                        LocalDateTime.of(2025,10,10,20,0),
                        vipLounge, pippin, 10));

                bookingDetailRepo.save(new BookingDetails(null, book1, sushi, 2));
                bookingDetailRepo.save(new BookingDetails(null, book1, edamame, 5));
                bookingDetailRepo.save(new BookingDetails(null, book2, tempura, 1));
                bookingDetailRepo.save(new BookingDetails(null, book3, ramen, 2));

            log.info("Seed-data added – customers: {}, dishes: {}, orders: {}, rooms: {}",
                    custRepo.count(), dishRepo.count(), orderRepo.count(), roomRepo.count());
        };
    }
}
