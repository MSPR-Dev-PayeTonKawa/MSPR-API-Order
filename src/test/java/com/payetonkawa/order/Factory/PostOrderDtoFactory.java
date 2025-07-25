package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.dto.PostOrderDto;

public class PostOrderDtoFactory {
    private static final Faker faker = new Faker();

    public static PostOrderDto generatePostOrderDto() {
        return PostOrderDto.builder()
                .clientId(faker.number().randomDigit())
                .clientLastname(faker.name().lastName())
                .clientFirstname(faker.name().firstName())
                .clientAddress(faker.address().fullAddress())
                .outdatedUserInformation(faker.bool().bool())
                .build();
    }
}
