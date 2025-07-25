package com.payetonkawa.order.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name= "\"order\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name="outdated_user_information")
    private Boolean outdatedUserInformation;
}
