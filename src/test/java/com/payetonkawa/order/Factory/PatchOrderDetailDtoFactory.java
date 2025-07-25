package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.dto.PatchOrderDetailDto;

public class PatchOrderDetailDtoFactory {
    private static final Faker faker = new Faker();

    public static PatchOrderDetailDto generatePatchOrderDetailDto() {
        return PatchOrderDetailDto.builder()
                .idOrderDetail(faker.number().randomDigit())
                .order(OrderFactory.generateOrder())
                .idProduct(faker.number().randomDigit())
                .unitPrice(String.valueOf(faker.number().numberBetween(10, 100)))
                .quantity(String.valueOf(faker.number().numberBetween(1, 10)))
                .outdatedProductInformation(faker.bool().bool())
                .build();
    }
}
