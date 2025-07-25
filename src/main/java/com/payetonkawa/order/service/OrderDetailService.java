package com.payetonkawa.order.service;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.config.RestConfig;
import com.payetonkawa.order.dto.ProductStockDto;
import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.repository.OrderDetailRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    private final MessagePublisher messagePublisher;

    private final RestTemplate restTemplate;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, MessagePublisher messagePublisher, RestTemplate restTemplate){
        this.orderDetailRepository = orderDetailRepository;
        this.messagePublisher = messagePublisher;
        this.restTemplate = restTemplate;
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

    private void enoughStock(Integer productId, Integer quantity){
        ResponseEntity<ProductStockDto> product = restTemplate.getForEntity(RestConfig.PRODUCT_API_URL + productId, ProductStockDto.class);
        switch (product.getStatusCode()){
            case HttpStatus.NOT_FOUND:
                throw new IllegalStateException("The product don't exist.");
            case HttpStatus.INTERNAL_SERVER_ERROR:
                throw new IllegalStateException("Something went wrong with the client api during the user verification.");
            case HttpStatus.OK:
                if(product.getBody().getStock() < quantity)
                    throw new IllegalStateException("Not enough stock.");
            default:
                break;
        }
    }

    public OrderDetail insert(OrderDetail orderDetail) throws IllegalStateException {
        if (orderDetail.getIdOrderDetail() != null && orderDetailRepository.existsById(orderDetail.getIdOrderDetail())) {
            throw new IllegalStateException("Entity already exists. Use update.");
        }
        enoughStock(orderDetail.getIdOrderDetail(), orderDetail.getQuantity());
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
            enoughStock(orderDetail.getIdOrderDetail(), orderDetail.getQuantity() - oldOrderDetail.getQuantity());
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
