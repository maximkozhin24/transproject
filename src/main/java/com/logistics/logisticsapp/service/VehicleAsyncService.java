package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.TaskStatus;
import com.logistics.logisticsapp.mapper.VehicleMapper;
import com.logistics.logisticsapp.repository.VehicleRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VehicleAsyncService {

    private final VehicleRepository vehicleRepository;

    public VehicleAsyncService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Async
    public void processGetAll(String taskId,
                              Map<String, TaskStatus> statusMap,
                              Map<String, List<VehicleResponseDto>> resultMap) {
        try {
            Thread.sleep(10000);

            List<VehicleResponseDto> result = vehicleRepository.findAll()
                .stream()
                .map(VehicleMapper::toDto)
                .toList();

            resultMap.put(taskId, result);
            statusMap.put(taskId, TaskStatus.COMPLETED);

        } catch (Exception e) {
            statusMap.put(taskId, TaskStatus.FAILED);
        }
    }
}
