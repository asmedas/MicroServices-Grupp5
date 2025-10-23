package com.zetterlund.wigell_sushi_api.dto;

   public class AddressDto {
       private Integer id;
       private String street;
       private String postalCode;
       private String city;

       // Getters och setters
       public Integer getId() {
           return id;
       }
       public void setId(Integer id) {
           this.id = id;
       }

       public String getStreet() {
           return street;
       }
       public void setStreet(String street) {
           this.street = street;
       }

       public String getPostalCode() {
           return postalCode;
       }
       public void setPostalCode(String postalCode) {
           this.postalCode = postalCode;
       }

       public String getCity() {
           return city;
       }
       public void setCity(String city) {
           this.city = city;
       }
   }
