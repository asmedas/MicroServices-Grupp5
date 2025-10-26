package com.miriam.travel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private final RestTemplate restTemplate;

    @Value("${converter.base-url}")
    private String baseUrl;

    @Value("${converter.api-key}")
    private String apiKey;

    private static final BigDecimal FALLBACK_SEK_TO_PLN = new BigDecimal("0.40");

    public CurrencyService() {
        this.restTemplate = new RestTemplate();
    }

    public BigDecimal convertSekToPln(BigDecimal amountSek) {
        try {
            String url = String.format("%s?to=PLN&amount=%s", baseUrl, amountSek);
            log.info("Requesting currency conversion from Converter API: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Double> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, Double.class
            );

            if (response.getBody() != null) {
                BigDecimal result = BigDecimal.valueOf(response.getBody()).setScale(2, RoundingMode.HALF_UP);
                log.info("Converter API responded with PLN value: {} (from {} SEK)", result, amountSek);
                return result;
            } else {
                log.error("Converter API returned empty response body!");
                throw new IllegalStateException("Empty response from Converter API");
            }

        } catch (Exception ex) {
            log.error("Failed to call Converter API: {} — using fallback rate ({} SEK = {} PLN)",
                    ex.getMessage(), amountSek, amountSek.multiply(FALLBACK_SEK_TO_PLN));
            return amountSek.multiply(FALLBACK_SEK_TO_PLN).setScale(2, RoundingMode.HALF_UP);
        }
    }
}

