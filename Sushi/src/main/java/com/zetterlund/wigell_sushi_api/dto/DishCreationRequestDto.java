package com.zetterlund.wigell_sushi_api.dto;

import java.math.BigDecimal;

public class DishCreationRequestDto {
       private String name;
       private BigDecimal priceInSek;
       private String description;
       
       // Getters och setters
       public String getName() {
           return name;
       }
       public void setName(String name) {
           this.name = name;
       }

       public BigDecimal getPriceInSek() {
           return priceInSek;
       }
       public void setPriceInSek(BigDecimal priceInSek) {
           this.priceInSek = priceInSek;
       }

       public String getDescription() {
           return description;
       }
       public void setDescription(String description) {
           this.description = description;
       }
   }
