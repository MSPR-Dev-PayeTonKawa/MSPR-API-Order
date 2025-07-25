package com.payetonkawa.order.service;

import com.paketonkawa.resources.message.Action;
import com.paketonkawa.resources.message.MessageDTO;
import com.paketonkawa.resources.message.Table;
import com.payetonkawa.order.Factory.OrderDetailFactory;
import com.payetonkawa.order.dto.ProductStockDto;
import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.repository.OrderDetailRepository;
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
class OrderDetailServiceTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderDetailService orderDetailService;

    @Test
    void findAll_shouldReturnList() {
        List<OrderDetail> list = OrderDetailFactory.generateOrderDetailList(2);
        when(orderDetailRepository.findAll()).thenReturn(list);

        List<OrderDetail> result = orderDetailService.findAll();

        assertEquals(2, result.size());
        verify(orderDetailRepository).findAll();
    }

    @Test
    void findById_shouldReturnOrderDetail() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        when(orderDetailRepository.findById(1)).thenReturn(Optional.of(od));

        Optional<OrderDetail> result = orderDetailService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(od, result.get());
    }

    @Test
    void findByOrderId_shouldReturnList() {
        List<OrderDetail> list = OrderDetailFactory.generateOrderDetailList(3);
        when(orderDetailRepository.findByOrder_IdOrder(1)).thenReturn(list);

        List<OrderDetail> result = orderDetailService.findByOrderId(1);

        assertEquals(3, result.size());
        verify(orderDetailRepository).findByOrder_IdOrder(1);
    }

    @Test
    void setOutdatedByProductId_shouldSetFlagAndSave() {
        OrderDetail od1 = OrderDetailFactory.generateOrderDetail();
        od1.setOutdatedProductInformation(false);
        OrderDetail od2 = OrderDetailFactory.generateOrderDetail();
        od2.setOutdatedProductInformation(false);
        List<OrderDetail> list = List.of(od1, od2);

        when(orderDetailRepository.findByIdProduct(1)).thenReturn(list);

        orderDetailService.setOutdatedByProductId(1);

        assertTrue(od1.getOutdatedProductInformation());
        assertTrue(od2.getOutdatedProductInformation());
        verify(orderDetailRepository, times(2)).save(any(OrderDetail.class));
    }

    @Test
    void insert_shouldSaveAndSendMessage() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(null);

        ProductStockDto stockDto = new ProductStockDto();
        stockDto.setStock(10);

        when(restTemplate.getForEntity(anyString(), eq(ProductStockDto.class)))
                .thenReturn(new ResponseEntity<>(stockDto, HttpStatus.OK));
        when(orderDetailRepository.save(od)).thenReturn(od);

        OrderDetail result = orderDetailService.insert(od);

        assertEquals(od, result);
        verify(messagePublisher).sendMessage(any(MessageDTO.class), eq("product"));
        verify(orderDetailRepository).save(od);
    }

    @Test
    void insert_shouldThrowIfIdExists() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(1);

        when(orderDetailRepository.existsById(1)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderDetailService.insert(od));
        assertEquals("Entity already exists. Use update.", ex.getMessage());
        verify(orderDetailRepository, never()).save(any());
    }

    @Test
    void insert_shouldThrowIfProductNotFound() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(null);

        when(restTemplate.getForEntity(anyString(), eq(ProductStockDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderDetailService.insert(od));
        assertEquals("The product don't exist.", ex.getMessage());
        verify(orderDetailRepository, never()).save(any());
    }

    @Test
    void insert_shouldThrowIfProductApiError() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(null);

        when(restTemplate.getForEntity(anyString(), eq(ProductStockDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderDetailService.insert(od));
        assertEquals("Something went wrong with the client api during the user verification.", ex.getMessage());
        verify(orderDetailRepository, never()).save(any());
    }

    @Test
    void insert_shouldThrowIfNotEnoughStock() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(null);

        ProductStockDto stockDto = new ProductStockDto();
        stockDto.setStock(od.getQuantity()-1);

        when(restTemplate.getForEntity(anyString(), eq(ProductStockDto.class)))
                .thenReturn(new ResponseEntity<>(stockDto, HttpStatus.OK));

        assertThrows(IllegalStateException.class, () -> orderDetailService.insert(od));
        verify(orderDetailRepository, never()).save(any());
    }

    @Test
    void update_shouldSaveIfExistsAndSendMessageIfQuantityChanged() {
        OrderDetail oldOd = OrderDetailFactory.generateOrderDetail();
        oldOd.setIdOrderDetail(1);
        oldOd.setQuantity(3);

        OrderDetail newOd = OrderDetailFactory.generateOrderDetail();
        newOd.setIdOrderDetail(1);
        newOd.setQuantity(5);

        ProductStockDto stockDto = new ProductStockDto();
        stockDto.setStock(10);

        when(orderDetailRepository.existsById(1)).thenReturn(true);
        when(orderDetailRepository.findById(1)).thenReturn(Optional.of(oldOd));
        when(restTemplate.getForEntity(anyString(), eq(ProductStockDto.class)))
                .thenReturn(new ResponseEntity<>(stockDto, HttpStatus.OK));
        when(orderDetailRepository.save(newOd)).thenReturn(newOd);

        OrderDetail result = orderDetailService.update(newOd);

        assertEquals(newOd, result);
        verify(messagePublisher).sendMessage(any(MessageDTO.class), eq("product"));
    }

    @Test
    void update_shouldSaveIfExistsAndNotSendMessageIfQuantityNotChanged() {
        OrderDetail oldOd = OrderDetailFactory.generateOrderDetail();
        oldOd.setIdOrderDetail(1);
        oldOd.setQuantity(3);

        OrderDetail newOd = OrderDetailFactory.generateOrderDetail();
        newOd.setIdOrderDetail(1);
        newOd.setQuantity(3);

        when(orderDetailRepository.existsById(1)).thenReturn(true);
        when(orderDetailRepository.findById(1)).thenReturn(Optional.of(oldOd));
        when(orderDetailRepository.save(newOd)).thenReturn(newOd);

        OrderDetail result = orderDetailService.update(newOd);

        assertEquals(newOd, result);
        verify(messagePublisher, never()).sendMessage(any(MessageDTO.class), anyString());
    }

    @Test
    void update_shouldThrowIfIdMissingOrNotExists() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(null);

        IllegalStateException ex1 = assertThrows(IllegalStateException.class, () -> orderDetailService.update(od));
        assertEquals("Entity doesn't exist. Use insert.", ex1.getMessage());

        od.setIdOrderDetail(1);
        when(orderDetailRepository.existsById(1)).thenReturn(false);

        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> orderDetailService.update(od));
        assertEquals("Entity doesn't exist. Use insert.", ex2.getMessage());
    }

    @Test
    void delete_shouldDeleteIfExistsAndSendMessage() {
        OrderDetail od = OrderDetailFactory.generateOrderDetail();
        od.setIdOrderDetail(1);
        od.setIdProduct(2);
        od.setQuantity(4);

        when(orderDetailRepository.existsById(1)).thenReturn(true);
        when(orderDetailRepository.findById(1)).thenReturn(Optional.of(od));

        orderDetailService.delete(1, true);

        verify(orderDetailRepository).deleteById(1);
        verify(messagePublisher).sendMessage(any(MessageDTO.class), eq("product"));
    }

    @Test
    void delete_shouldThrowIfNotExists() {
        when(orderDetailRepository.existsById(1)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderDetailService.delete(1, false));
        assertEquals("Entity doesn't exist. Can't delete.", ex.getMessage());

        verify(orderDetailRepository, never()).deleteById(any());
    }
}
