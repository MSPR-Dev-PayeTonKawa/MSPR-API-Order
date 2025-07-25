package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.dto.PostOrderDetailDto;
import com.payetonkawa.order.entity.Order;

public class PostOrderDetailDtoFactory {
    private static final Faker faker = new Faker();

    public static PostOrderDetailDto generatePostOrderDetailDto(Order order) {
        return PostOrderDetailDto.builder()
                .order(order)
                .idProduct(faker.number().randomDigit())
                .unitPrice(String.valueOf(faker.number().numberBetween(10, 1000)))
                .quantity(String.valueOf(faker.number().numberBetween(1, 10)))
                .outdatedProductInformation(faker.bool().bool())
                .build();
    }
}
