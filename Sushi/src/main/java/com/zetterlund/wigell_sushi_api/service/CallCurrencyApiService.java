package com.zetterlund.wigell_sushi_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


@Service
public class CallCurrencyApiService {

    private static final Logger log = LoggerFactory.getLogger(CallCurrencyApiService.class);

    private final RestTemplate restTemplate;

    @Value("${currency.api.url}")
    private String apiUrl;

    @Value("${currency.api-key}")
    private String apiKey;

    public CallCurrencyApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal convertFromSEKToJPY(BigDecimal amount) {
        log.debug("Converting amount from SEK to JPY with microservice");
        try {
            String url = String.format("%s?to=%s&amount=%s", apiUrl, "JPY", amount.toPlainString());

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
            log.error("Error calling converter microservice", e);
            throw new RuntimeException("Currency conversion failed", e);
        }
    }
}
