package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteVehicleCargoRepository extends JpaRepository<RouteVehicleCargo, Long> {

    List<RouteVehicleCargo> findByOrderId(Long orderId);
    List<RouteVehicleCargo> findAllByOrder_Id(Long orderId);
    boolean existsByCargo_Id(Long cargoId);
    List<RouteVehicleCargo> findAllByCargo_Id(Long cargoId);
    List<RouteVehicleCargo> findAllByRoute_Id(Long routeId);
}