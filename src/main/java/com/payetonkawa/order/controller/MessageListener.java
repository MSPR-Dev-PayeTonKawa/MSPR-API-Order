package com.payetonkawa.order.controller;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.config.RabbitConfig;
import com.payetonkawa.order.service.OrderDetailService;
import com.payetonkawa.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class MessageListener {
    private OrderService orderService;
    private OrderDetailService orderDetailService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(MessageDTO message) {
        // Update or Delete of a user causes all previous orders from him to have outdated informations
        if((message.getAction() == Action.UPDATE || message.getAction() == Action.DELETE) &&
                message.getTable() == Table.CLIENT &&
                message.getInformation().get("clientId") instanceof Integer clientId){
            orderService.setOutdatedByClientId(clientId);
        }
        // Update or Delete of a user causes all previous orders from him to have outdated informations
        if((message.getAction() == Action.UPDATE || message.getAction() == Action.DELETE) &&
                message.getTable() == Table.PRODUCT &&
                message.getInformation().get("productId") instanceof Integer productId){
            orderDetailService.setOutdatedByProductId(productId);
        }
    }
}
