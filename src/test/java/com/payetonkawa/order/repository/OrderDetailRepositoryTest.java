package com.payetonkawa.order.repository;

import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.Factory.OrderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderDetailRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    void findByOrder_IdOrder_shouldReturnDetails() {
        Order unmanagedOrder = OrderFactory.generateOrder();
        unmanagedOrder.setIdOrder(null);
        Order savedOrder = orderRepository.save(unmanagedOrder);

        OrderDetail detail1 = OrderDetail.builder()
                .order(savedOrder)
                .idProduct(101)
                .quantity(2)
                .unitPrice(19)
                .outdatedProductInformation(false)
                .build();

        OrderDetail detail2 = OrderDetail.builder()
                .order(savedOrder)
                .idProduct(102)
                .quantity(3)
                .unitPrice(29)
                .outdatedProductInformation(false)
                .build();

        orderDetailRepository.saveAll(List.of(detail1, detail2));

        List<OrderDetail> results = orderDetailRepository.findByOrder_IdOrder(savedOrder.getIdOrder());

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(d -> d.getOrder().getIdOrder().equals(savedOrder.getIdOrder()));
    }

    @Test
    void findByIdProduct_shouldReturnDetails() {
        Order unmanagedOrder = OrderFactory.generateOrder();
        unmanagedOrder.setIdOrder(null);
        Order savedOrder = orderRepository.save(unmanagedOrder);

        OrderDetail detail = OrderDetail.builder()
                .order(savedOrder)
                .idProduct(123)
                .quantity(1)
                .unitPrice(9)
                .outdatedProductInformation(false)
                .build();

        orderDetailRepository.save(detail);

        List<OrderDetail> results = orderDetailRepository.findByIdProduct(123);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIdProduct()).isEqualTo(123);
    }
}
