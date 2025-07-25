package com.payetonkawa.order.dto;

import com.payetonkawa.order.entity.Order;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchOrderDetailDto {
    private Integer idOrderDetail;

    private Order order;

    private Integer idProduct;

    private String unitPrice;

    private String quantity;

    private Boolean outdatedProductInformation;
}
