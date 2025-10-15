package com.andreas.wigellmcrental.mapper;

import com.andreas.wigellmcrental.dto.AddressDto;
import com.andreas.wigellmcrental.dto.BikeDto;
import com.andreas.wigellmcrental.dto.BookingDto;
import com.andreas.wigellmcrental.dto.CustomerDto;
import com.andreas.wigellmcrental.entity.*;

public class Mapper {

    public static BikeDto toBikeDto(Bike b) {
        return new BikeDto(
                b.getId(),
                b.getBrand(),
                b.getModel(),
                b.getYear(),
                b.getPricePerDay(),
                b.isAvailable()
        );
    }

    public static BookingDto toBookingDto(Booking bk) {
        return new BookingDto(
                bk.getId(),
                bk.getCustomer().getId(),
                bk.getBike().getId(),
                bk.getBike().getModel(),
                bk.getStartDate(),
                bk.getEndDate(),
                bk.getTotalPriceSek(),
                bk.getTotalPriceGbp(),
                bk.getStatus()
        );
    }

    public static CustomerDto toCustomerDto(Customer c) {
        return new CustomerDto(
                c.getId(),
                c.getUsername(),
                c.getName(),
                c.getEmail(),
                c.getPhone()
        );
    }

    public static AddressDto toAddressDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getCountry(),
                address.getPostalCode()
        );
    }



}
