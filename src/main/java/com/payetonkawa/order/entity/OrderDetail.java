package com.payetonkawa.order.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    private String unitPrice;

    @Column(name="quantity")
    private String quantity;
}
