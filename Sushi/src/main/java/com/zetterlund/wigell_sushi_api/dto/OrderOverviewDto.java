package com.zetterlund.wigell_sushi_api.dto;

   public class OrderOverviewDto {
       private Integer orderId;
       private String customerName;
       private double totalPriceInSek;

       // Getters och setters
       public Integer getOrderId() {
           return orderId;
       }
       public void setOrderId(Integer orderId) {
           this.orderId = orderId;
       }

       public String getCustomerName() {
           return customerName;
       }
       public void setCustomerName(String customerName) {
           this.customerName = customerName;
       }

       public double getTotalPriceInSek() {
           return totalPriceInSek;
       }
       public void setTotalPriceInSek(double totalPriceInSek) {
           this.totalPriceInSek = totalPriceInSek;
       }
   }
