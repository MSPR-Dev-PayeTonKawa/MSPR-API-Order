package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.entity.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailFactory {
    private static final Faker faker = new Faker();

    public static OrderDetail generateOrderDetail() {
        return OrderDetail.builder()
                .idOrderDetail(faker.number().randomDigit())
                .order(OrderFactory.generateOrder())
                .idProduct(faker.number().randomDigit())
                .unitPrice(faker.number().numberBetween(10, 100))
                .quantity(faker.number().numberBetween(1, 10))
                .outdatedProductInformation(faker.bool().bool())
                .build();
    }

    public static List<OrderDetail> generateOrderDetailList(int number) {
        List<OrderDetail> details = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            details.add(generateOrderDetail());
        }
        return details;
    }
}
