package com.payetonkawa.order.dto;

import java.sql.Date;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchOrderDto {
    private Integer idOrder;

    private Integer clientId;

    private String clientLastname;

    private String clientFirstname;

    private String clientAddress;
}
