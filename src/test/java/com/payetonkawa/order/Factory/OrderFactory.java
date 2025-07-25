package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.entity.Order;
import jakarta.persistence.Column;

import java.util.ArrayList;
import java.util.List;

public class OrderFactory {
    private static final Faker faker = new Faker();

    public static Order generateOrder(){
        return Order.builder()
                .idOrder(faker.number().randomDigit())
                .clientId(faker.number().randomDigit())
                .clientLastname(faker.name().lastName())
                .clientFirstname(faker.name().firstName())
                .clientAddress(faker.address().fullAddress())
                .outdatedUserInformation(faker.bool().bool()).build();
    }

    public static List<Order> generateOrderList(int number){
        List<Order> orders = new ArrayList<>();
        for(int i = 0; i < number; i++){
            orders.add(generateOrder());
        }
        return orders;
    }
}
