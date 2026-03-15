package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoRepository extends JpaRepository<Cargo, Long> {
}
