package com.payetonkawa.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostOrderDto {
    private Integer clientId;

    private String clientLastname;

    private String clientFirstname;

    private String clientAddress;

    private Boolean outdatedUserInformation;
}
