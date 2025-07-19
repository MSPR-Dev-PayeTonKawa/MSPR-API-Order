package com.payetonkawa.order.entity;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name= "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id_order")
    private Integer idOrder;

    @Column(name="client_id")
    private Integer clientId;

    @Column(name="client_lastname")
    private String clientLastname;

    @Column(name="client_firstname")
    private String clientFirstname;

    @Column(name="client_address")
    private String clientAddress;
}
