package com.payetonkawa.order.dto;

import com.payetonkawa.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostOrderDetailDto {
    private Order order;

    private Integer idProduct;

    private String unitPrice;

    private String quantity;

    private Boolean outdatedProductInformation;
}
