package com.payetonkawa.order.service;

import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.repository.OrderDetailRepository;
import com.payetonkawa.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    private final RabbitTemplate rabbitTemplate;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, RabbitTemplate rabbitTemplate){
        this.orderDetailRepository = orderDetailRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.queue.product:product.sync.queue}")
    private String productQueue;

    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> findById(Integer id) {
        return orderDetailRepository.findById(id);
    }

    public List<OrderDetail> findByOrderDetailId(Integer orderDetailId) {
        return orderDetailRepository.findByOrder_IdOrder(orderDetailId);
    }

    public OrderDetail insert(OrderDetail orderDetail) throws IllegalStateException {
        if (orderDetail.getIdOrderDetail() != null && orderDetailRepository.existsById(orderDetail.getIdOrderDetail())) {
            throw new IllegalStateException("Entity already exists. Use update.");
        }
        // TODO actions sur le messages broker pour synchroniser les autres bdd
        return orderDetailRepository.save(orderDetail);
    }

    public OrderDetail update(OrderDetail orderDetail) throws IllegalStateException {
//        if (orderDetail.getIdOrderDetail() == null || !orderDetailRepository.existsById(orderDetail.getIdOrderDetail())) {
//            throw new IllegalStateException("Entity doesn't exist. Use insert.");
//        }
        String msg = "test message in mq from app";
        rabbitTemplate.convertAndSend(productQueue, msg);
        // TODO actions sur le messages broker pour synchroniser les autres bdd
        return orderDetailRepository.save(orderDetail);
    }

    public void delete(Integer id){
        // TODO actions sur le messages broker pour synchroniser les autres bdd
        orderDetailRepository.deleteById(id);
    }
}
