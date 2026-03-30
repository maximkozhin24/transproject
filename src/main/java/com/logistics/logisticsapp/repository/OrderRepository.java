package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.routeVehicleCargoList rvc
        LEFT JOIN FETCH rvc.route
        LEFT JOIN FETCH rvc.cargo
        LEFT JOIN FETCH rvc.vehicle
        """)
    List<Order> findAllWithRelations();
    List<Order> findAllByClient_Id(Long clientId);
}