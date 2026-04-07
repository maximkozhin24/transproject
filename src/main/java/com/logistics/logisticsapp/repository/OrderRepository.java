package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    JOIN FETCH o.routeVehicleCargoList rvc
    JOIN FETCH rvc.cargo c
    LEFT JOIN FETCH rvc.route
    LEFT JOIN FETCH rvc.vehicle
    WHERE c.name = :cargoName
           """)
    List<Order> findOrdersByCargoName(String cargoName);
    @Query(value = """
    SELECT 
        o.id as order_id,
        o.status as order_status,

        c.id as cargo_id,
        c.name as cargo_name,

        r.id as route_id,
        r.start_location,
        r.end_location,

        v.id as vehicle_id,
        v.plate_number

    FROM orders o
    JOIN route_vehicle_cargo rvc ON o.id = rvc.order_id
    JOIN cargo c ON rvc.cargo_id = c.id
    LEFT JOIN routes r ON rvc.route_id = r.id
    LEFT JOIN vehicles v ON rvc.vehicle_id = v.id
    WHERE c.name = :cargoName
         """, nativeQuery = true)
    List<Object[]> findOrdersFlat(@Param("cargoName") String cargoName);
}