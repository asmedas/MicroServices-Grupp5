package com.zetterlund.wigell_sushi_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConverterService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConverterService.class);

    private final RestTemplate restTemplate;

    @Value("${currency.converter.api.url}")
    private String apiUrl;

    @Value("${currency.converter.api.token}")
    private String apiToken;

    public CurrencyConverterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Double convertCurrency(String toCurrency, double amount) {
        String url = String.format("%s?to=%s&amount=%.2f", apiUrl, toCurrency, amount);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            logger.info("Requesting currency conversion: {}", url);
            Double convertedValue = restTemplate.getForObject(url, Double.class, requestEntity);
            logger.info("Currency conversion successful: {} {} -> {} {}", amount, "SEK", convertedValue, toCurrency);
            return convertedValue;
        } catch (Exception e) {
            logger.error("Error during currency conversion", e);
            throw new RuntimeException("Currency conversion failed", e);
        }
    }
}
