package com.payetonkawa.order.service;

import java.util.*;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.config.RestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.repository.OrderRepository;

import jakarta.transaction.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final MessagePublisher messagePublisher;

    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, MessagePublisher messagePublisher, RestTemplate restTemplate){
        this.orderRepository = orderRepository;
        this.messagePublisher = messagePublisher;
        this.restTemplate = restTemplate;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByClientId(Integer clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public void setOutdatedByClientId(Integer clientId){
        for(Order order : orderRepository.findByClientId(clientId)){
            order.setOutdatedUserInformation(true);
            orderRepository.save(order);
        }
    }

    private void clientExist(Integer clientId) throws IllegalStateException{
        ResponseEntity<Object> client = restTemplate.getForEntity(RestConfig.CLIENT_API_URL + clientId, Object.class);
        switch (client.getStatusCode()){
            case HttpStatus.NOT_FOUND:
                throw new IllegalStateException("The user don't exist.");
            case HttpStatus.INTERNAL_SERVER_ERROR:
                throw new IllegalStateException("Something went wrong with the client api during the user verification.");
            default:
                break;
        }
    }

    public Order insert(Order order) throws IllegalStateException {
        if (order.getIdOrder() != null && orderRepository.existsById(order.getIdOrder())) {
            throw new IllegalStateException("Entity already exists. Use update.");
        }
        clientExist(order.getClientId());
        Order newOrder = orderRepository.save(order);
        Map<String, Object> information = new HashMap<>();
        information.put("clientId", newOrder.getClientId());
        messagePublisher.sendMessage(new MessageDTO(Action.INSERT, Table.ORDER, information), "client");
        return newOrder;
    }

    public Order update(Order order) throws IllegalStateException {
        if (order.getIdOrder() == null || !orderRepository.existsById(order.getIdOrder())) {
            throw new IllegalStateException("Entity doesn't exist. Use insert.");
        }
        Order oldOrder = orderRepository.findById(order.getIdOrder()).get();
        if(!oldOrder.getClientId().equals(order.getClientId())){
            clientExist(order.getClientId());
            Map<String, Object> information = new HashMap<>();
            information.put("oldClientId", oldOrder.getClientId());
            information.put("newClientId", order.getClientId());
            messagePublisher.sendMessage(new MessageDTO(Action.UPDATE, Table.ORDER, information), "client");
        }
        return orderRepository.save(order);
    }

    public void delete(Integer id){
        if (!orderRepository.existsById(id)) {
            throw new IllegalStateException("Entity doesn't exist. Can't delete.");
        }
        Order order = orderRepository.findById(id).get();
        Map<String, Object> information = new HashMap<>();
        information.put("clientId", order.getClientId());
        messagePublisher.sendMessage(new MessageDTO(Action.DELETE, Table.ORDER, information), "client");
        orderRepository.deleteById(id);
    }
}
