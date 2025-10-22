package com.strom.wigellPadel.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Configuration
public class DataInitializer {

    CommandLineRunner dataInitializer() {
        return args -> {

        }
    }
}
