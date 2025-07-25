package com.payetonkawa.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostOrderDto {
    private Integer clientId;

    private String clientLastname;

    private String clientFirstname;

    private String clientAddress;

    private boolean outdatedUserInformation;
}
