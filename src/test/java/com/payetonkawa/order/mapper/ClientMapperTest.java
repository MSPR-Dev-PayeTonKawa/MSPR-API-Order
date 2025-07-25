package com.payetonkawa.order.mapper;

import com.payetonkawa.order.Factory.OrderFactory;
import com.payetonkawa.order.Factory.PatchOrderDtoFactory;
import com.payetonkawa.order.Factory.PostOrderDtoFactory;
import com.payetonkawa.order.dto.PatchOrderDto;
import com.payetonkawa.order.dto.PostOrderDto;
import com.payetonkawa.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @Test
    void fromPostDto(){
        PostOrderDto postOrderDto = PostOrderDtoFactory.generatePostOrderDto();
        Order order = orderMapper.fromPostDto(postOrderDto);
        assertNull(order.getIdOrder());
        assertEquals(order.getClientId(), postOrderDto.getClientId());
        assertEquals(order.getClientLastname(), postOrderDto.getClientLastname());
        assertEquals(order.getClientFirstname(), postOrderDto.getClientFirstname());
        assertEquals(order.getClientAddress(), postOrderDto.getClientAddress());
        assertEquals(order.getOutdatedUserInformation(), postOrderDto.getOutdatedUserInformation());
    }

    @Test
    void fromPostDto_null(){
        assertNull(orderMapper.fromPostDto(null));
    }

    @Test
    void fromPatchDto(){
        PatchOrderDto patchOrderDto = PatchOrderDtoFactory.generatePatchOrderDto();
        Order order = orderMapper.fromPatchDto(patchOrderDto);
        assertEquals(order.getIdOrder(), patchOrderDto.getIdOrder());
        assertEquals(order.getClientId(), patchOrderDto.getClientId());
        assertEquals(order.getClientLastname(), patchOrderDto.getClientLastname());
        assertEquals(order.getClientFirstname(), patchOrderDto.getClientFirstname());
        assertEquals(order.getClientAddress(), patchOrderDto.getClientAddress());
        assertEquals(order.getOutdatedUserInformation(), patchOrderDto.getOutdatedUserInformation());
    }

    @Test
    void fromPatchDto_null(){
        assertNull(orderMapper.fromPatchDto(null));
    }

    @Test
    void toPostDto(){
        Order order = OrderFactory.generateOrder();
        PostOrderDto postOrderDto = orderMapper.toPostDto(order);
        assertEquals(order.getClientId(), postOrderDto.getClientId());
        assertEquals(order.getClientLastname(), postOrderDto.getClientLastname());
        assertEquals(order.getClientFirstname(), postOrderDto.getClientFirstname());
        assertEquals(order.getClientAddress(), postOrderDto.getClientAddress());
        assertEquals(order.getOutdatedUserInformation(), postOrderDto.getOutdatedUserInformation());
    }

    @Test
    void toPostDto_null(){
        assertNull(orderMapper.toPostDto(null));
    }

    @Test
    void toPatchDto(){
        Order order = OrderFactory.generateOrder();
        PatchOrderDto patchOrderDto = orderMapper.toPatchDto(order);
        assertEquals(order.getIdOrder(), patchOrderDto.getIdOrder());
        assertEquals(order.getClientId(), patchOrderDto.getClientId());
        assertEquals(order.getClientLastname(), patchOrderDto.getClientLastname());
        assertEquals(order.getClientFirstname(), patchOrderDto.getClientFirstname());
        assertEquals(order.getClientAddress(), patchOrderDto.getClientAddress());
        assertEquals(order.getOutdatedUserInformation(), patchOrderDto.getOutdatedUserInformation());
    }

    @Test
    void toPatchDto_null(){
        assertNull(orderMapper.toPatchDto(null));
    }
}