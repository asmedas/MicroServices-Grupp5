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
import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private final String baseUrl;
    private final String apiKey;


    private static final BigDecimal FALLBACK_SEK_TO_PLN = new BigDecimal("0.40");

    public CurrencyService(
            @Value("${converter.base-url}") String baseUrl,
            @Value("${converter.api-key}") String apiKey
    ) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public BigDecimal convertSekToPln(BigDecimal amountSek) {
        try {
            String url = String.format("%s?from=SEK&to=PLN&amount=%s", baseUrl, amountSek);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, Map.class
            );


            if (response.getBody() != null && response.getBody().get("result") != null) {
                double result = Double.parseDouble(response.getBody().get("result").toString());
                return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
            } else {
                throw new IllegalStateException("Empty response body");
            }

        } catch (Exception ex) {
            log.warn("Currency converter unavailable, fallback rate used: {}", ex.getMessage());
            return amountSek.multiply(FALLBACK_SEK_TO_PLN).setScale(2, RoundingMode.HALF_UP);
        }
    }
}

