package com.sebbe.converter_microservice.controller;

import com.sebbe.converter_microservice.converter.CurrencyConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConvertController {

    private CurrencyConverter currencyConverter;

    public ConvertController(CurrencyConverter currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> convert(@RequestParam String to, @RequestParam double amount) {
        return ResponseEntity.ok(currencyConverter.convertFromSEK(to, amount));
    }

}
