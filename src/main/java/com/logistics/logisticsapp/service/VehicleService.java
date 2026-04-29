package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.RequestCounter;
import com.logistics.logisticsapp.dto.AssignVehicleDto;
import com.logistics.logisticsapp.dto.RaceConditionReport;
import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.entity.TaskStatus;
import com.logistics.logisticsapp.entity.Vehicle;
import com.logistics.logisticsapp.exception.ConflictException;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.mapper.VehicleMapper;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import com.logistics.logisticsapp.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class VehicleService {
    private final VehicleAsyncService asyncService;
    private final VehicleRepository vehicleRepository;
    private final RouteVehicleCargoRepository rvcRepository;
    private final RequestCounter counter;
    private final ExecutorService vehicleExecutor;
    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);
    private final Map<String, TaskStatus> statusMap = new ConcurrentHashMap<>();
    private final Map<String, List<VehicleResponseDto>> resultMap = new ConcurrentHashMap<>();

    public VehicleService(VehicleRepository vehicleRepository,
                          RouteVehicleCargoRepository rvcRepository,
                          VehicleAsyncService asyncService,
                          RequestCounter counter,
                          ExecutorService vehicleExecutor) {
        this.vehicleRepository = vehicleRepository;
        this.rvcRepository = rvcRepository;
        this.asyncService = asyncService;
        this.counter = counter;
        this.vehicleExecutor = vehicleExecutor;
    }

    public VehicleResponseDto create(VehicleRequestDto dto) {
        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new ConflictException("PlateNumber already exists");
        }

        Vehicle vehicle = VehicleMapper.toEntity(dto);
        vehicle = vehicleRepository.save(vehicle);

        return VehicleMapper.toDto(vehicle);
    }

    public List<VehicleResponseDto> getAll() {
        int count = counter.incrementAndGet();
        LOG.info("Запрос getAll вызван: {} раз", count);
        return vehicleRepository.findAll()
            .stream()
            .map(VehicleMapper::toDto)
            .toList();
    }
    private static final String VEHICLE_NOT_FOUND = "Vehicle not found";
    public VehicleResponseDto getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(VEHICLE_NOT_FOUND));

        return VehicleMapper.toDto(vehicle);
    }

    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(VEHICLE_NOT_FOUND));

        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());

        return VehicleMapper.toDto(vehicleRepository.save(vehicle));
    }

    public void assignVehicle(AssignVehicleDto dto) {

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
            .orElseThrow(() -> new ResourceNotFoundException(VEHICLE_NOT_FOUND));

        List<RouteVehicleCargo> relations =
            rvcRepository.findByOrderId(dto.getOrderId());

        if (relations.isEmpty()) {
            throw new IllegalStateException("No relations found for this order");
        }

        for (RouteVehicleCargo rvc : relations) {
            rvc.setVehicle(vehicle);
            rvcRepository.save(rvc);
        }
    }

    public void delete(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new ResourceNotFoundException(VEHICLE_NOT_FOUND));

        List<RouteVehicleCargo> relations = rvcRepository.findAllByVehicleId(vehicleId);

        for (RouteVehicleCargo rvc : relations) {
            rvc.setVehicle(null);
        }

        vehicleRepository.delete(vehicle);
    }

    public String getAllAsync() {
        String taskId = UUID.randomUUID().toString();

        statusMap.put(taskId, TaskStatus.IN_PROGRESS);

        asyncService.processGetAll(taskId, statusMap, resultMap);

        return taskId;
    }

    public TaskStatus getStatus(String taskId) {
        return statusMap.get(taskId);
    }

    public List<VehicleResponseDto> getResult(String taskId) {
        return resultMap.get(taskId);
    }

    public RaceConditionReport runBadRaceConditionDemo() {

        int tasks = 500;

        List<VehicleResponseDto> sharedList = new ArrayList<>();

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < tasks; i++) {

            futures.add(vehicleExecutor.submit(() -> {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }

                sharedList.add(new VehicleResponseDto());
            }));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {
                Thread.currentThread().interrupt();
            }
        }

        int expected = tasks;
        int actual = sharedList.size();

        return new RaceConditionReport(expected, actual);
    }

    public RaceConditionReport runFixedRaceConditionDemo() {

        int tasks = 500;

        List<VehicleResponseDto> safeList =
            java.util.Collections.synchronizedList(new ArrayList<>());

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < tasks; i++) {

            futures.add(vehicleExecutor.submit(() -> {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }

                safeList.add(new VehicleResponseDto());
            }));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {
                Thread.currentThread().interrupt();
            }
        }

        int expected = tasks;
        int actual = safeList.size();

        return new RaceConditionReport(expected, actual);
    }
}