package com.strom.wigellPadel.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConverterClient {

    private final RestTemplate restTemplate;

    @Value("${converter.service.url}")
    private String converterUrl;

    @Value("${internal.api.key}")
    private String apiKey;

    public CurrencyConverterClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double convertToEUR(double amount) {
        String url = converterUrl + "?to=EUR&amount=" + amount;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.GET, entity, Double.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Currency conversion failed with status: " + response.getStatusCode());
        }
    }
}