package com.payetonkawa.order.repository;

import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.Factory.OrderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findByClientId_shouldReturnOrders() {
        Order order1 = OrderFactory.generateOrder();
        order1.setIdOrder(null);
        order1.setClientId(42);
        Order order2 = OrderFactory.generateOrder();
        order2.setIdOrder(null);
        order2.setClientId(42);

        orderRepository.saveAll(List.of(order1, order2));

        List<Order> results = orderRepository.findByClientId(42);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(o -> o.getClientId().equals(42));
    }

}
