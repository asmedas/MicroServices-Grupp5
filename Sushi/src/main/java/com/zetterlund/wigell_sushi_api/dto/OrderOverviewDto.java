package com.zetterlund.wigell_sushi_api.dto;

import java.math.BigDecimal;

public class OrderOverviewDto {
       private Integer orderId;
       private String customerName;
       private BigDecimal totalPriceInSek;

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

       public BigDecimal getTotalPriceInSek() {
           return totalPriceInSek;
       }
       public void setTotalPriceInSek(BigDecimal totalPriceInSek) {
           this.totalPriceInSek = totalPriceInSek;
       }
   }
