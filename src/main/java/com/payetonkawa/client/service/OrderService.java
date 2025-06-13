package com.payetonkawa.order.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.product:product.sync.queue}")
    private String productQueue;

    public List<Order> findall() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    public Order insert(Order order) throws IllegalStateException {
        if (order.getIdOrder() != null && orderRepository.existsById(order.getIdOrder())) {
            throw new IllegalStateException("Entity already exists. Use update.");
        }
        // TODO actions sur le messages broker pour synchroniser les autres bdd
        return orderRepository.save(order);
    }

    public Order update(Order order) throws IllegalStateException {
//        if (order.getIdOrder() == null || !orderRepository.existsById(order.getIdOrder())) {
//            throw new IllegalStateException("Entity doesn't exist. Use insert.");
//        }
        String msg = "test message in mq from app";
        rabbitTemplate.convertAndSend(productQueue, msg);
        // TODO actions sur le messages broker pour synchroniser les autres bdd
        return orderRepository.save(order);
    }

    public void delete(Integer id){
        // TODO actions sur le messages broker pour synchroniser les autres bdd
        orderRepository.deleteById(id);
    }
}
