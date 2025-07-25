package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.dto.ProductStockDto;

public class ProductStockDtoFactory {
    private static final Faker faker = new Faker();

    public static ProductStockDto generateProductStockDto() {
        return ProductStockDto.builder()
                .stock(faker.number().numberBetween(0, 1000))
                .build();
    }
}
