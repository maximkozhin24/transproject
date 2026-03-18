package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}