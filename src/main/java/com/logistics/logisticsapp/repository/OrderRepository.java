package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
