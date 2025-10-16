package com.zetterlund.wigell_sushi_api.dto;

   public class DishCreationRequestDto {
       private String name;
       private Double priceInSek;
       private String description;
       
       // Getters och setters
       public String getName() {
           return name;
       }
       public void setName(String name) {
           this.name = name;
       }

       public Double getPriceInSek() {
           return priceInSek;
       }
       public void setPriceInSek(Double priceInSek) {
           this.priceInSek = priceInSek;
       }

       public String getDescription() {
           return description;
       }
       public void setDescription(String description) {
           this.description = description;
       }
   }
