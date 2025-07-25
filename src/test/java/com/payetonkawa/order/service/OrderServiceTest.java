package com.payetonkawa.order.service;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.Factory.OrderFactory;
import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void findAll_shouldReturnList() {
        List<Order> list = OrderFactory.generateOrderList(2);
        when(orderRepository.findAll()).thenReturn(list);

        List<Order> result = orderService.findAll();

        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void findById_shouldReturnOrder() {
        Order order = OrderFactory.generateOrder();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    void findByClientId_shouldReturnList() {
        List<Order> list = OrderFactory.generateOrderList(3);
        when(orderRepository.findByClientId(1)).thenReturn(list);

        List<Order> result = orderService.findByClientId(1);

        assertEquals(3, result.size());
        verify(orderRepository).findByClientId(1);
    }

    @Test
    void setOutdatedByClientId_shouldSetFlagAndSave() {
        Order order1 = OrderFactory.generateOrder();
        order1.setClientId(1);
        order1.setOutdatedUserInformation(false);
        Order order2 = OrderFactory.generateOrder();
        order2.setClientId(1);
        order2.setOutdatedUserInformation(false);
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findByClientId(1)).thenReturn(orders);

        orderService.setOutdatedByClientId(1);

        assertTrue(order1.getOutdatedUserInformation());
        assertTrue(order2.getOutdatedUserInformation());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void insert_shouldSaveOrderAndSendMessage() {
        Order order = OrderFactory.generateOrder();
        order.setIdOrder(null);

        // Simulate client exists (HTTP 200 OK)
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.insert(order);

        assertEquals(order, result);
        verify(orderRepository).save(order);
        verify(messagePublisher).sendMessage(any(MessageDTO.class), eq("client"));
    }

    @Test
    void insert_shouldThrowIfOrderIdExists() {
        Order order = OrderFactory.generateOrder();
        order.setIdOrder(1);

        when(orderRepository.existsById(1)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.insert(order));
        assertEquals("Entity already exists. Use update.", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void insert_shouldThrowIfClientNotFound() {
        Order order = OrderFactory.generateOrder();
        order.setIdOrder(null);

        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.insert(order));
        assertEquals("The user don't exist.", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void insert_shouldThrowIfClientApiError() {
        Order order = OrderFactory.generateOrder();
        order.setIdOrder(null);

        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.insert(order));
        assertEquals("Something went wrong with the client api during the user verification.", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void update_shouldSaveIfExistsAndSendMessageIfClientChanged() {
        Order oldOrder = OrderFactory.generateOrder();
        oldOrder.setIdOrder(1);
        oldOrder.setClientId(1);

        Order newOrder = OrderFactory.generateOrder();
        newOrder.setIdOrder(1);
        newOrder.setClientId(2);

        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.findById(1)).thenReturn(Optional.of(oldOrder));
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(orderRepository.save(newOrder)).thenReturn(newOrder);

        Order result = orderService.update(newOrder);

        assertEquals(newOrder, result);
        verify(messagePublisher).sendMessage(any(MessageDTO.class), eq("client"));
    }

    @Test
    void update_shouldSaveIfExistsAndNotSendMessageIfClientNotChanged() {
        Order oldOrder = OrderFactory.generateOrder();
        oldOrder.setIdOrder(1);
        oldOrder.setClientId(1);

        Order newOrder = OrderFactory.generateOrder();
        newOrder.setIdOrder(1);
        newOrder.setClientId(1);

        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.findById(1)).thenReturn(Optional.of(oldOrder));
        when(orderRepository.save(newOrder)).thenReturn(newOrder);

        Order result = orderService.update(newOrder);

        assertEquals(newOrder, result);
        verify(messagePublisher, never()).sendMessage(any(MessageDTO.class), anyString());
    }

    @Test
    void update_shouldThrowIfIdMissingOrNotExists() {
        Order order = OrderFactory.generateOrder();
        order.setIdOrder(null);

        IllegalStateException ex1 = assertThrows(IllegalStateException.class, () -> orderService.update(order));
        assertEquals("Entity doesn't exist. Use insert.", ex1.getMessage());

        order.setIdOrder(1);
        when(orderRepository.existsById(1)).thenReturn(false);

        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> orderService.update(order));
        assertEquals("Entity doesn't exist. Use insert.", ex2.getMessage());
    }

    @Test
    void delete_shouldDeleteIfExistsAndSendMessage() {
        Order order = OrderFactory.generateOrder();
        order.setIdOrder(1);
        order.setClientId(1);

        when(orderRepository.existsById(1)).thenReturn(true);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        orderService.delete(1);

        verify(orderRepository).deleteById(1);
        verify(messagePublisher).sendMessage(any(MessageDTO.class), eq("client"));
    }

    @Test
    void delete_shouldThrowIfNotExists() {
        when(orderRepository.existsById(1)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.delete(1));
        assertEquals("Entity doesn't exist. Can't delete.", ex.getMessage());

        verify(orderRepository, never()).deleteById(any());
    }
}
