package com.payetonkawa.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payetonkawa.order.Factory.OrderFactory;
import com.payetonkawa.order.dto.PatchOrderDto;
import com.payetonkawa.order.dto.PostOrderDto;
import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.mapper.OrderMapper;
import com.payetonkawa.order.mapper.OrderMapperImpl;
import com.payetonkawa.order.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.MockConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @AfterEach
    void resetMocks() {
        reset(orderService);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }

        @Bean
        public OrderMapper orderMapper() {
            return new OrderMapperImpl();
        }
    }

    @Test
    void findAll_success() throws Exception {
        List<Order> orders = OrderFactory.generateOrderList(2);
        when(orderService.findAll()).thenReturn(orders);
        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idOrder").value(orders.get(0).getIdOrder()))
                .andExpect(jsonPath("$[1].idOrder").value(orders.get(1).getIdOrder()));
    }

    @Test
    void findAll_error() throws Exception {
        when(orderService.findAll()).thenThrow(new RuntimeException());
        mockMvc.perform(get("/order"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void find_success() throws Exception {
        Order order = OrderFactory.generateOrder();
        when(orderService.findById(1)).thenReturn(Optional.of(order));
        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(order.getIdOrder()))
                .andExpect(jsonPath("$.clientId").value(order.getClientId()))
                .andExpect(jsonPath("$.clientLastname").value(order.getClientLastname()))
                .andExpect(jsonPath("$.clientFirstname").value(order.getClientFirstname()))
                .andExpect(jsonPath("$.clientAddress").value(order.getClientAddress()))
                .andExpect(jsonPath("$.outdatedUserInformation").value(order.getOutdatedUserInformation()));
    }

    @Test
    void find_notFound() throws Exception {
        when(orderService.findById(1)).thenReturn(Optional.empty());
        mockMvc.perform(get("/order/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void find_error() throws Exception {
        when(orderService.findById(1)).thenThrow(new RuntimeException());
        mockMvc.perform(get("/order/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void create_success() throws Exception {
        Order order = OrderFactory.generateOrder();
        PostOrderDto postOrderDto = orderMapper.toPostDto(order);
        when(orderService.insert(any())).thenReturn(order);
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(order.getIdOrder()))
                .andExpect(jsonPath("$.clientId").value(order.getClientId()))
                .andExpect(jsonPath("$.clientLastname").value(order.getClientLastname()))
                .andExpect(jsonPath("$.clientFirstname").value(order.getClientFirstname()))
                .andExpect(jsonPath("$.clientAddress").value(order.getClientAddress()))
                .andExpect(jsonPath("$.outdatedUserInformation").value(order.getOutdatedUserInformation()));
    }

    @Test
    void create_badRequest() throws Exception {
        Order order = OrderFactory.generateOrder();
        PostOrderDto postOrderDto = orderMapper.toPostDto(order);
        when(orderService.insert(any())).thenThrow(new IllegalStateException());
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postOrderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_error() throws Exception {
        Order order = OrderFactory.generateOrder();
        PostOrderDto postOrderDto = orderMapper.toPostDto(order);
        when(orderService.insert(any())).thenThrow(new RuntimeException());
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postOrderDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void update_success() throws Exception {
        Order order = OrderFactory.generateOrder();
        PatchOrderDto patchOrderDto = orderMapper.toPatchDto(order);
        when(orderService.update(any())).thenReturn(order);
        mockMvc.perform(put("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(order.getIdOrder()))
                .andExpect(jsonPath("$.clientId").value(order.getClientId()))
                .andExpect(jsonPath("$.clientLastname").value(order.getClientLastname()))
                .andExpect(jsonPath("$.clientFirstname").value(order.getClientFirstname()))
                .andExpect(jsonPath("$.clientAddress").value(order.getClientAddress()))
                .andExpect(jsonPath("$.outdatedUserInformation").value(order.getOutdatedUserInformation()));
    }

    @Test
    void update_badRequest() throws Exception {
        Order order = OrderFactory.generateOrder();
        PatchOrderDto patchOrderDto = orderMapper.toPatchDto(order);
        when(orderService.update(any())).thenThrow(new IllegalStateException());
        mockMvc.perform(put("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchOrderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_error() throws Exception {
        Order order = OrderFactory.generateOrder();
        PatchOrderDto patchOrderDto = orderMapper.toPatchDto(order);
        when(orderService.update(any())).thenThrow(new RuntimeException());
        mockMvc.perform(put("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchOrderDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(orderService).delete(1);
        mockMvc.perform(delete("/order/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_error() throws Exception {
        doThrow(new RuntimeException()).when(orderService).delete(1);
        mockMvc.perform(delete("/order/1"))
                .andExpect(status().isInternalServerError());
    }
}
