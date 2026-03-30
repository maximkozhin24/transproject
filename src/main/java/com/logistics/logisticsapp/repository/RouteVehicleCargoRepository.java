package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteVehicleCargoRepository extends JpaRepository<RouteVehicleCargo, Long> {

    List<RouteVehicleCargo> findByOrderId(Long orderId);
    boolean existsByCargoIdAndOrderIdNot(Long cargoId, Long orderId);
    long countByOrderId(Long orderId);
    List<RouteVehicleCargo> findByCargoId(Long cargoId);
    List<RouteVehicleCargo> findByRouteId(Long routeId);
    List<RouteVehicleCargo> findAllByOrder_Id(Long orderId);
    boolean existsByCargo_Id(Long cargoId);
    boolean existsByRoute_Id(Long routeId);
    List<RouteVehicleCargo> findAllByCargo_Id(Long cargoId);
    List<RouteVehicleCargo> findAllByRoute_Id(Long routeId);
}