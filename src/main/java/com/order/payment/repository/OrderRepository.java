package com.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.payment.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
