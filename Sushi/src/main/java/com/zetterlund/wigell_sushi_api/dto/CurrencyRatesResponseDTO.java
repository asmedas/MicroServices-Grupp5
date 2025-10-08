package com.zetterlund.wigell_sushi_api.dto;

import java.util.Map;

public class CurrencyRatesResponseDTO {
    private String result;
    private Map<String, Double> rates;

    // Getters och setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    // Getter och Setter för 'rates'
    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
