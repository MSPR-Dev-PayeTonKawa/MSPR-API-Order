package com.payetonkawa.order.controller;

import com.payetonkawa.order.dto.PatchOrderDetailDto;
import com.payetonkawa.order.dto.PostOrderDetailDto;
import com.payetonkawa.order.entity.OrderDetail;
import com.payetonkawa.order.mapper.OrderDetailMapper;
import com.payetonkawa.order.service.OrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-detail")
@AllArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    private final OrderDetailMapper orderDetailMapper;

    @GetMapping()
    public ResponseEntity<List<OrderDetail>> findAll() {
        try {
            return new ResponseEntity<>(orderDetailService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetail> find(@PathVariable Integer id) {
        try {
            Optional<OrderDetail> orderDetail = orderDetailService.findById(id);
            return orderDetail.map(detail -> new ResponseEntity<>(detail, HttpStatus.OK)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetail>> findByOrder(@PathVariable Integer orderId) {
        try {
            return new ResponseEntity<>(orderDetailService.findByOrderDetailId(orderId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<OrderDetail> create(@RequestBody PostOrderDetailDto dto) {
        try {
            return new ResponseEntity<>(orderDetailService.insert(orderDetailMapper.fromPostDto(dto)), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping()
    public ResponseEntity<OrderDetail> update(@RequestBody PatchOrderDetailDto dto) {
        try {
            return new ResponseEntity<>(orderDetailService.update(orderDetailMapper.fromPatchDto(dto)), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
