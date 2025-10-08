package com.zetterlund.wigell_sushi_api.prices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetterlund.wigell_sushi_api.dto.CurrencyRatesResponseDTO; // Import av DTO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
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

    public double getSEKToYenExchangeRate() {
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);

            CurrencyRatesResponseDTO currencyRates = objectMapper.readValue(response, CurrencyRatesResponseDTO.class);

            if ("success".equals(currencyRates.getResult())) {
                return currencyRates.getRates().get("JPY"); // Hämta växelkurs för JPY
            } else {
                throw new IllegalStateException("Error in API response: " + currencyRates.getResult());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching currency rates", e);
        }
    }
}
