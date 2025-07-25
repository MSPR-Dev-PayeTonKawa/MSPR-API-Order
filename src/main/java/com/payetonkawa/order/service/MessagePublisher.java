package com.payetonkawa.order.service;

import com.paketonkawa.resources.message.MessageDTO;
import com.payetonkawa.order.config.RabbitConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MessageDTO message, String targetAPI){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    targetAPI,
                    message
            );
        }
        catch (Exception e){
            log.info(e.getMessage());
        }
    }
}
