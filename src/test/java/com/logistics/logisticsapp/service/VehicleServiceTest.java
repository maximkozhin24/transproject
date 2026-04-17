package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.AssignVehicleDto;
import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.entity.Vehicle;
import com.logistics.logisticsapp.exception.ConflictException;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import com.logistics.logisticsapp.repository.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RouteVehicleCargoRepository rvcRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleRequestDto requestDto;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setPlateNumber("ABC123");
        vehicle.setModel("Tesla");
        vehicle.setCapacity(1000.0);

        requestDto = new VehicleRequestDto();
        requestDto.setPlateNumber("ABC123");
        requestDto.setModel("Tesla");
        requestDto.setCapacity(1000.0);
    }

    // ---------------- CREATE ----------------

    @Test
    void create_shouldSaveVehicle() {

        when(vehicleRepository.existsByPlateNumber("ABC123"))
            .thenReturn(false);

        when(vehicleRepository.save(any(Vehicle.class)))
            .thenReturn(vehicle);

        VehicleResponseDto result = vehicleService.create(requestDto);

        assertNotNull(result);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void create_shouldThrowIfPlateExists() {

        when(vehicleRepository.existsByPlateNumber("ABC123"))
            .thenReturn(true);

        assertThrows(ConflictException.class,
            () -> vehicleService.create(requestDto));
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAll_shouldReturnList() {

        when(vehicleRepository.findAll())
            .thenReturn(List.of(vehicle));

        List<VehicleResponseDto> result = vehicleService.getAll();

        assertEquals(1, result.size());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getById_shouldReturnVehicle() {

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

        VehicleResponseDto result = vehicleService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_shouldThrowIfNotFound() {

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.getById(1L));
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_shouldModifyVehicle() {

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

        when(vehicleRepository.save(any()))
            .thenReturn(vehicle);

        VehicleResponseDto result =
            vehicleService.update(1L, requestDto);

        assertNotNull(result);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void update_shouldThrowIfNotFound() {

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.update(1L, requestDto));
    }

    // ---------------- ASSIGN VEHICLE ----------------

    @Test
    void assignVehicle_shouldAssignToAllRelations() {

        AssignVehicleDto dto = new AssignVehicleDto();
        dto.setVehicleId(1L);
        dto.setOrderId(10L);

        RouteVehicleCargo rvc1 = new RouteVehicleCargo();
        RouteVehicleCargo rvc2 = new RouteVehicleCargo();

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

        when(rvcRepository.findByOrderId(10L))
            .thenReturn(List.of(rvc1, rvc2));

        vehicleService.assignVehicle(dto);

        assertEquals(vehicle, rvc1.getVehicle());
        assertEquals(vehicle, rvc2.getVehicle());

        verify(rvcRepository, times(2)).save(any());
    }

    @Test
    void assignVehicle_shouldThrowIfVehicleNotFound() {

        AssignVehicleDto dto = new AssignVehicleDto();
        dto.setVehicleId(1L);
        dto.setOrderId(10L);

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.assignVehicle(dto));
    }

    @Test
    void assignVehicle_shouldThrowIfNoRelations() {

        AssignVehicleDto dto = new AssignVehicleDto();
        dto.setVehicleId(1L);
        dto.setOrderId(10L);

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

        when(rvcRepository.findByOrderId(10L))
            .thenReturn(List.of());

        assertThrows(IllegalStateException.class,
            () -> vehicleService.assignVehicle(dto));
    }

    // ---------------- DELETE ----------------

    @Test
    void delete_shouldClearRelationsAndDeleteVehicle() {

        RouteVehicleCargo rvc1 = new RouteVehicleCargo();
        RouteVehicleCargo rvc2 = new RouteVehicleCargo();

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.of(vehicle));

        when(rvcRepository.findAllByVehicleId(1L))
            .thenReturn(List.of(rvc1, rvc2));

        vehicleService.delete(1L);

        assertNull(rvc1.getVehicle());
        assertNull(rvc2.getVehicle());

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void delete_shouldThrowIfNotFound() {

        when(vehicleRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> vehicleService.delete(1L));
    }
}
