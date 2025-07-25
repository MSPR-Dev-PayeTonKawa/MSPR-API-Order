package com.payetonkawa.order.Factory;

import com.github.javafaker.Faker;
import com.payetonkawa.order.dto.PatchOrderDto;

public class PatchOrderDtoFactory {
    private static final Faker faker = new Faker();

    public static PatchOrderDto generatePatchOrderDto() {
        return PatchOrderDto.builder()
                .idOrder(faker.number().randomDigit())
                .clientId(faker.number().randomDigit())
                .clientLastname(faker.name().lastName())
                .clientFirstname(faker.name().firstName())
                .clientAddress(faker.address().fullAddress())
                .outdatedUserInformation(faker.bool().bool())
                .build();
    }
}
