package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByModel(String model);
}