package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteVehicleCargoRepository extends JpaRepository<RouteVehicleCargo, Long> {
}