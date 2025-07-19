package com.payetonkawa.order.repository;

import com.payetonkawa.order.entity.Order;
import com.payetonkawa.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder_IdOrder(Integer idOrder);
}
