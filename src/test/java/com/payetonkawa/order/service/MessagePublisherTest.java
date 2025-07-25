package com.payetonkawa.order.service;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.config.RabbitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static org.mockito.Mockito.*;

class MessagePublisherTest {

    private RabbitTemplate rabbitTemplate;
    private MessagePublisher messagePublisher;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        messagePublisher = new MessagePublisher(rabbitTemplate);
    }

    @Test
    void sendMessage_shouldSendCorrectly() {
        MessageDTO message = new MessageDTO(Action.INSERT, Table.ORDER, Map.of("clientId", 1));
        String targetAPI = "client";

        messagePublisher.sendMessage(message, targetAPI);

        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE_NAME, targetAPI, message);
    }

    @Test
    void sendMessage_shouldCatchAndLogException() {
        MessageDTO message = new MessageDTO(Action.UPDATE, Table.ORDER_DETAIL, Map.of("productId", 2));
        String targetAPI = "product";

        doThrow(new RuntimeException("RabbitMQ failure")).when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), (Object) any());

        messagePublisher.sendMessage(message, targetAPI);

        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE_NAME, targetAPI, message);
        // We don't assert logs here, but verify that exception is caught and doesn't throw
    }
}
