package com.payetonkawa.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payetonkawa.order.Factory.OrderDetailFactory;
import com.payetonkawa.order.dto.PatchOrderDetailDto;
import com.payetonkawa.order.dto.PostOrderDetailDto;
import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.mapper.OrderDetailMapper;
import com.payetonkawa.order.mapper.OrderDetailMapperImpl;
import com.payetonkawa.order.service.OrderDetailService;
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

@WebMvcTest(OrderDetailController.class)
@Import(OrderDetailControllerTest.MockConfig.class)
class OrderDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @AfterEach
    void resetMocks() {
        reset(orderDetailService);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public OrderDetailService orderDetailService() {
            return Mockito.mock(OrderDetailService.class);
        }

        @Bean
        public OrderDetailMapper orderDetailMapper() {
            return new OrderDetailMapperImpl();
        }
    }

    @Test
    void findAll_success() throws Exception {
        List<OrderDetail> details = OrderDetailFactory.generateOrderDetailList(2);
        when(orderDetailService.findAll()).thenReturn(details);

        mockMvc.perform(get("/order-detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(details.size()))
                .andExpect(jsonPath("$[0].idOrderDetail").value(details.get(0).getIdOrderDetail()))
                .andExpect(jsonPath("$[1].idOrderDetail").value(details.get(1).getIdOrderDetail()));
    }

    @Test
    void findAll_error() throws Exception {
        when(orderDetailService.findAll()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/order-detail"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void find_success() throws Exception {
        OrderDetail detail = OrderDetailFactory.generateOrderDetail();
        when(orderDetailService.findById(1)).thenReturn(Optional.of(detail));

        mockMvc.perform(get("/order-detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrderDetail").value(detail.getIdOrderDetail()))
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andExpect(jsonPath("$.idProduct").value(detail.getIdProduct()))
                .andExpect(jsonPath("$.unitPrice").value(detail.getUnitPrice()))
                .andExpect(jsonPath("$.quantity").value(detail.getQuantity()))
                .andExpect(jsonPath("$.outdatedProductInformation").value(detail.getOutdatedProductInformation()));
    }

    @Test
    void find_notFound() throws Exception {
        when(orderDetailService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/order-detail/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void find_error() throws Exception {
        when(orderDetailService.findById(1)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/order-detail/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findByOrder_success() throws Exception {
        List<OrderDetail> details = OrderDetailFactory.generateOrderDetailList(2);
        when(orderDetailService.findByOrderId(1)).thenReturn(details);

        mockMvc.perform(get("/order-detail/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(details.size()))
                .andExpect(jsonPath("$[0].order.idOrder").value(details.get(0).getOrder().getIdOrder()));
    }

    @Test
    void findByOrder_error() throws Exception {
        when(orderDetailService.findByOrderId(1)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/order-detail/order/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void create_success() throws Exception {
        OrderDetail detail = OrderDetailFactory.generateOrderDetail();
        PostOrderDetailDto dto = orderDetailMapper.toPostDto(detail);

        when(orderDetailService.insert(any())).thenReturn(detail);

        mockMvc.perform(post("/order-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrderDetail").value(detail.getIdOrderDetail()));
    }

    @Test
    void create_badRequest() throws Exception {
        PostOrderDetailDto dto = new PostOrderDetailDto(); // Provide invalid data or empty

        when(orderDetailService.insert(any())).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/order-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_error() throws Exception {
        PostOrderDetailDto dto = new PostOrderDetailDto();

        when(orderDetailService.insert(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/order-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void update_success() throws Exception {
        OrderDetail detail = OrderDetailFactory.generateOrderDetail();
        PatchOrderDetailDto dto = orderDetailMapper.toPatchDto(detail);

        when(orderDetailService.update(any())).thenReturn(detail);

        mockMvc.perform(put("/order-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrderDetail").value(detail.getIdOrderDetail()));
    }

    @Test
    void update_badRequest() throws Exception {
        PatchOrderDetailDto dto = new PatchOrderDetailDto();

        when(orderDetailService.update(any())).thenThrow(new IllegalStateException());

        mockMvc.perform(put("/order-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_error() throws Exception {
        PatchOrderDetailDto dto = new PatchOrderDetailDto();

        when(orderDetailService.update(any())).thenThrow(new RuntimeException());

        mockMvc.perform(put("/order-detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(orderDetailService).delete(1, false);

        mockMvc.perform(delete("/order-detail/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_error() throws Exception {
        doThrow(new RuntimeException()).when(orderDetailService).delete(1, false);

        mockMvc.perform(delete("/order-detail/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void cancel_success() throws Exception {
        doNothing().when(orderDetailService).delete(1, true);

        mockMvc.perform(delete("/order-detail/1/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void cancel_error() throws Exception {
        doThrow(new RuntimeException()).when(orderDetailService).delete(1, true);

        mockMvc.perform(delete("/order-detail/1/cancel"))
                .andExpect(status().isInternalServerError());
    }
}