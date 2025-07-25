package com.payetonkawa.order.service;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.repository.OrderDetailRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    private final MessagePublisher messagePublisher;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, MessagePublisher messagePublisher){
        this.orderDetailRepository = orderDetailRepository;
        this.messagePublisher = messagePublisher;
    }

    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> findById(Integer id) {
        return orderDetailRepository.findById(id);
    }

    public List<OrderDetail> findByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrder_IdOrder(orderId);
    }

    public void setOutdatedByProductId(Integer clientId){
        for(OrderDetail orderDetail : orderDetailRepository.findByIdProduct(clientId)){
            orderDetail.setOutdatedProductInformation(true);
            orderDetailRepository.save(orderDetail);
        }
    }

    public OrderDetail insert(OrderDetail orderDetail) throws IllegalStateException {
        if (orderDetail.getIdOrderDetail() != null && orderDetailRepository.existsById(orderDetail.getIdOrderDetail())) {
            throw new IllegalStateException("Entity already exists. Use update.");
        }
        Map<String, Object> information = new HashMap<>();
        information.put("productId", orderDetail.getIdProduct());
        information.put("quantity", orderDetail.getQuantity());
        messagePublisher.sendMessage(new MessageDTO(Action.INSERT, Table.ORDER_DETAIL, information), "product");
        return orderDetailRepository.save(orderDetail);
    }

    public OrderDetail update(OrderDetail orderDetail) throws IllegalStateException {
        if (orderDetail.getIdOrderDetail() == null || !orderDetailRepository.existsById(orderDetail.getIdOrderDetail())) {
            throw new IllegalStateException("Entity doesn't exist. Use insert.");
        }
        OrderDetail oldOrderDetail = orderDetailRepository.findById(orderDetail.getIdOrderDetail()).get();
        if(!oldOrderDetail.getQuantity().equals(orderDetail.getQuantity())){
            Map<String, Object> information = new HashMap<>();
            information.put("productId", orderDetail.getIdProduct());
            information.put("oldQuantity", oldOrderDetail.getQuantity());
            information.put("newQuantity", orderDetail.getQuantity());
            messagePublisher.sendMessage(new MessageDTO(Action.UPDATE, Table.ORDER_DETAIL, information), "product");
        }
        return orderDetailRepository.save(orderDetail);
    }

    public void delete(Integer id, boolean canceled){
        if (!orderDetailRepository.existsById(id)) {
            throw new IllegalStateException("Entity doesn't exist. Can't delete.");
        }
        OrderDetail orderDetail = orderDetailRepository.findById(id).get();
        Map<String, Object> information = new HashMap<>();
        information.put("productId", orderDetail.getIdProduct());
        information.put("quantity", orderDetail.getQuantity());
        information.put("canceled", canceled);
        messagePublisher.sendMessage(new MessageDTO(Action.DELETE, Table.ORDER_DETAIL, information), "product");
        orderDetailRepository.deleteById(id);
    }
}
