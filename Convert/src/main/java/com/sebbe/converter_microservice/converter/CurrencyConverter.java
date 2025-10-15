package com.sebbe.converter_microservice.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebbe.converter_microservice.dto.CurrencyRatesResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConverter {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConverter.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${currency.api.url}")
    private String apiUrl;

    public CurrencyConverter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public double convertFromSEK(String to, double amount) {
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            CurrencyRatesResponseDTO currencyRates = objectMapper.readValue(response, CurrencyRatesResponseDTO.class);

            if (!"success".equals(currencyRates.getResult())) {
                throw new IllegalStateException("Error in API response: " + currencyRates.getResult());
            }

            Double rate = currencyRates.getRates().get(to.toUpperCase());
            if (rate == null) {
                throw new IllegalArgumentException("Unsupported target currency: " + to);
            }

            return amount * rate;
        } catch (Exception e) {
            logger.error("Error fetching or converting currency", e);
            throw new RuntimeException("Currency conversion failed", e);
        }
    }
}
