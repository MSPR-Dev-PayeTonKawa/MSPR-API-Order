package com.payetonkawa.order.controller;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.service.OrderDetailService;
import com.payetonkawa.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

class MessageListenerTest {

    private OrderService orderService;
    private OrderDetailService orderDetailService;
    private MessageListener messageListener;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderDetailService = mock(OrderDetailService.class);
        messageListener = new MessageListener(orderService, orderDetailService);
    }

    @Test
    void receiveMessage_shouldTriggerOrderServiceOnClientUpdate() {
        MessageDTO message = new MessageDTO(Action.UPDATE, Table.CLIENT, Map.of("clientId", 42));

        messageListener.receiveMessage(message);

        verify(orderService).setOutdatedByClientId(42);
        verifyNoMoreInteractions(orderService, orderDetailService);
    }

    @Test
    void receiveMessage_shouldTriggerOrderServiceOnClientDelete() {
        MessageDTO message = new MessageDTO(Action.DELETE, Table.CLIENT, Map.of("clientId", 99));

        messageListener.receiveMessage(message);

        verify(orderService).setOutdatedByClientId(99);
        verifyNoMoreInteractions(orderService, orderDetailService);
    }

    @Test
    void receiveMessage_shouldTriggerOrderDetailServiceOnProductUpdate() {
        MessageDTO message = new MessageDTO(Action.UPDATE, Table.PRODUCT, Map.of("productId", 5));

        messageListener.receiveMessage(message);

        verify(orderDetailService).setOutdatedByProductId(5);
        verifyNoMoreInteractions(orderService, orderDetailService);
    }

    @Test
    void receiveMessage_shouldTriggerOrderDetailServiceOnProductDelete() {
        MessageDTO message = new MessageDTO(Action.DELETE, Table.PRODUCT, Map.of("productId", 7));

        messageListener.receiveMessage(message);

        verify(orderDetailService).setOutdatedByProductId(7);
        verifyNoMoreInteractions(orderService, orderDetailService);
    }

    @Test
    void receiveMessage_shouldIgnoreUnknownMessage() {
        MessageDTO message = new MessageDTO(Action.INSERT, Table.CLIENT, Map.of("clientId", 7));

        messageListener.receiveMessage(message);

        verifyNoInteractions(orderService, orderDetailService);
    }

    @Test
    void receiveMessage_shouldIgnoreInvalidIdType() {
        MessageDTO message = new MessageDTO(Action.UPDATE, Table.CLIENT, Map.of("clientId", "notAnInteger"));

        messageListener.receiveMessage(message);

        verifyNoInteractions(orderService, orderDetailService);
    }
}
