package com.payetonkawa.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name= "order_detail")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id_order_detail")
    private Integer idOrderDetail;

    @ManyToOne
    @JoinColumn(name="id_order")
    private Order order;

    @Column(name="id_product")
    private Integer idProduct;

    @Column(name="unit_price")
    private Integer unitPrice;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name="outdated_product_information")
    private Boolean outdatedProductInformation;
}
