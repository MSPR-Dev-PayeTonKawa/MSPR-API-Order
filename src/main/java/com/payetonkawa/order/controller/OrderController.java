package com.payetonkawa.order.controller;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.payetonkawa.order.dto.PatchOrderDto;
import com.payetonkawa.order.dto.PostOrderDto;
import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.mapper.OrderMapper;
import com.payetonkawa.order.service.OrderService;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping()
    public ResponseEntity<List<Order>> findAll() {
        try {
            return new ResponseEntity<>(orderService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> find(@PathVariable Integer id) {
        try {
            Optional<Order> order = orderService.findById(id);
            if (order.isPresent()) {
                return new ResponseEntity<>(order.get(), HttpStatus.OK);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<Order> create(@RequestBody PostOrderDto dto) {
        try {
            return new ResponseEntity<>(orderService.insert(orderMapper.fromPostDto(dto)), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping()
    public ResponseEntity<Order> update(@RequestBody PatchOrderDto dto) {
        try {
            return new ResponseEntity<>(orderService.update(orderMapper.fromPatchDto(dto)), HttpStatus.OK);
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
