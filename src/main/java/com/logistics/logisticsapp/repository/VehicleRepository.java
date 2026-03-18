package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}