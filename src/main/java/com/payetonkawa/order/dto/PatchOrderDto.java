package com.payetonkawa.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchOrderDto {
    private Integer idOrder;

    private Integer clientId;

    private String clientLastname;

    private String clientFirstname;

    private String clientAddress;

    private Boolean outdatedUserInformation;
}
