package com.sebbe.cinema.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


@Service
public class CallCurrencyApiService {

    private static final Logger logger = LoggerFactory.getLogger(CallCurrencyApiService.class);

    private final RestTemplate restTemplate;
    private final Logger log = LoggerFactory.getLogger(CallCurrencyApiService.class);

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api-key}")
    private String apiKey;

    public CallCurrencyApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal convertFromSEKToUsd(BigDecimal amount) {
        log.debug("Converting amount from SEK to USD with microservice");
        try {
            String url = String.format("%s?to=%s&amount=%s", apiUrl, "USD", amount.toPlainString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Double> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Double.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("Unexpected response from converter API");
                throw new IllegalStateException("Unexpected response from converter API");
            }

            return BigDecimal.valueOf(response.getBody());

        } catch (Exception e) {
            logger.error("Error calling converter microservice", e);
            throw new RuntimeException("Currency conversion failed", e);
        }
    }

}
