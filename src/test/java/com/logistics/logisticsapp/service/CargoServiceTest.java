package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.repository.CargoRepository;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
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
class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RouteVehicleCargoRepository rvcRepository;

    @InjectMocks
    private CargoService cargoService;

    private Cargo cargo;
    private CargoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        cargo = new Cargo();
        cargo.setId(1L);
        cargo.setName("Test");
        cargo.setWeight(100.0);

        requestDto = new CargoRequestDto();
        requestDto.setName("Test");
        requestDto.setWeight(100.0);
    }

    // ---------------- CREATE ----------------

    @Test
    void create_shouldSaveAndReturnDto() {
        when(cargoRepository.save(any(Cargo.class))).thenAnswer(invocation -> {
            Cargo c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CargoResponseDto result = cargoService.create(requestDto);

        assertNotNull(result);
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAll_shouldReturnList() {
        when(cargoRepository.findAll()).thenReturn(List.of(cargo));

        List<CargoResponseDto> result = cargoService.getAll();

        assertEquals(1, result.size());
        verify(cargoRepository).findAll();
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getById_shouldReturnCargo() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));

        CargoResponseDto result = cargoService.getById(1L);

        assertNotNull(result);
        verify(cargoRepository).findById(1L);
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> cargoService.getById(1L));
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_shouldModifyAndSave() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);

        CargoResponseDto result = cargoService.update(1L, requestDto);

        assertNotNull(result);
        verify(cargoRepository).save(cargo);
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> cargoService.update(1L, requestDto));
    }

    // ---------------- DELETE (cascade logic) ----------------

    @Test
    void delete_shouldRemoveCargoAndRelations() {
        RouteVehicleCargo rvc = mock(RouteVehicleCargo.class);
        Order order = new Order();
        order.setId(10L);

        when(rvc.getOrder()).thenReturn(order);

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(rvcRepository.findAllByCargoId(1L)).thenReturn(List.of(rvc));
        when(rvcRepository.findAllByOrderId(10L)).thenReturn(List.of());

        cargoService.delete(1L);

        verify(rvcRepository).deleteAll(anyList());
        verify(orderRepository).delete(order);
        verify(cargoRepository).delete(cargo);
    }

    @Test
    void delete_shouldThrowIfCargoNotFound() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> cargoService.delete(1L));
    }

    // ---------------- BULK NON-TRANSACTIONAL ----------------

    @Test
    void createBulkNoTransaction_shouldThrowMidway() {
        List<CargoRequestDto> dtos = List.of(
            requestDto,
            requestDto,
            requestDto
        );

        assertThrows(IllegalStateException.class,
            () -> cargoService.createCargosBulkNoTransaction(dtos));
    }

    // ---------------- BULK TRANSACTIONAL ----------------

    @Test
    void createBulkTransactional_shouldRollbackOnException() {
        List<CargoRequestDto> dtos = List.of(
            requestDto,
            requestDto,
            requestDto
        );

        assertThrows(IllegalStateException.class,
            () -> cargoService.createCargosBulkTransactional(dtos));

        // при реальной БД проверялся бы rollback
        verify(cargoRepository, atLeastOnce()).save(any());
    }
}