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
    List<Order> findAllByClientId(Long clientId);
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN o.routeVehicleCargoList rvc
        JOIN rvc.cargo c
        WHERE c.name = :cargoName
          """)
    List<Order> findOrdersByCargoName(String cargoName);
    @Query(value = """
        SELECT DISTINCT o.*
        FROM orders o
        JOIN route_vehicle_cargo rvc ON o.id = rvc.order_id
        JOIN cargo c ON rvc.cargo_id = c.id
        WHERE c.name = :cargoName
          """, nativeQuery = true)
    List<Order> findOrdersByCargoNameNative(String cargoName);
}