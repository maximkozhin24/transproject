package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.*;
import com.logistics.logisticsapp.entity.*;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock ClientRepository clientRepository;
    @Mock RouteRepository routeRepository;
    @Mock CargoRepository cargoRepository;
    @Mock RouteVehicleCargoRepository rvcRepository;

    @InjectMocks OrderService orderService;

    // ---------------- HELPERS ----------------

    private OrderRequestDto validDto() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setPrice(100.0);
        dto.setStatus(OrderStatus.NEW);
        dto.setClientId(1L);

        RouteVehicleCargoRequestDto item = new RouteVehicleCargoRequestDto();
        item.setRouteId(10L);
        item.setCargoId(20L);

        dto.setItems(List.of(item));
        return dto;
    }

    private OrderRequestDto dtoWithoutItems() {
        OrderRequestDto dto = validDto();
        dto.setItems(null);
        return dto;
    }

    private OrderRequestDto dtoWithEmptyItems() {
        OrderRequestDto dto = validDto();
        dto.setItems(List.of());
        return dto;
    }

    // ---------------- CREATE EDGE CASES ----------------

    @Test
    void create_shouldThrowIfItemsNull() {
        assertThrows(IllegalStateException.class,
            () -> orderService.create(dtoWithoutItems()));
    }

    @Test
    void create_shouldThrowIfItemsEmpty() {

        OrderRequestDto dto = validDto();
        dto.setItems(List.of()); // ← единственная причина ошибки

        assertThrows(IllegalStateException.class,
            () -> orderService.create(dto));
    }
    @Test
    void create_shouldThrowIfClientNotFound() {

        OrderRequestDto dto = validDto();

        when(clientRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.create(dto));
    }

    @Test
    void create_shouldThrowIfRouteNotFound() {

        OrderRequestDto dto = validDto();

        when(clientRepository.findById(1L))
            .thenReturn(Optional.of(new Client()));

        when(orderRepository.save(any()))
            .thenReturn(new Order());

        when(routeRepository.findById(10L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.create(dto));
    }

    @Test
    void create_shouldThrowIfCargoNotFound() {

        OrderRequestDto dto = validDto();

        when(clientRepository.findById(1L))
            .thenReturn(Optional.of(new Client()));

        when(orderRepository.save(any()))
            .thenReturn(new Order());

        when(routeRepository.findById(10L))
            .thenReturn(Optional.of(new Route()));

        when(cargoRepository.findById(20L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.create(dto));
    }

    @Test
    void create_shouldWorkHappyPath() {

        OrderRequestDto dto = validDto();

        when(clientRepository.findById(1L))
            .thenReturn(Optional.of(new Client()));

        when(orderRepository.save(any()))
            .thenReturn(new Order());

        when(routeRepository.findById(10L))
            .thenReturn(Optional.of(new Route()));

        when(cargoRepository.findById(20L))
            .thenReturn(Optional.of(new Cargo()));

        OrderResponseDto result = orderService.create(dto);

        assertNotNull(result);
        verify(rvcRepository).save(any(RouteVehicleCargo.class));
    }

    // ---------------- UPDATE clientId NULL branch ----------------

    @Test
    void update_shouldNotSetClientIfNull() {

        Order order = new Order();

        OrderRequestDto dto = validDto();
        dto.setClientId(null);

        when(orderRepository.findById(1L))
            .thenReturn(Optional.of(order));

        when(orderRepository.save(any()))
            .thenReturn(order);

        OrderResponseDto result = orderService.update(1L, dto);

        assertNotNull(result);
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void update_shouldSetClientIfNotNull() {

        Order order = new Order();

        OrderRequestDto dto = validDto();

        when(orderRepository.findById(1L))
            .thenReturn(Optional.of(order));

        when(clientRepository.findById(1L))
            .thenReturn(Optional.of(new Client()));

        when(orderRepository.save(any()))
            .thenReturn(order);

        orderService.update(1L, dto);

        verify(clientRepository).findById(1L);
    }

    // ---------------- DELETE usedElsewhere branches ----------------

    @Test
    void delete_shouldNotDeleteCargoIfUsedElsewhere() {

        Order order = new Order();
        order.setId(1L);

        Cargo cargo = new Cargo();
        cargo.setId(100L);

        RouteVehicleCargo rvc = mock(RouteVehicleCargo.class);
        when(rvc.getCargo()).thenReturn(cargo);

        when(orderRepository.findById(1L))
            .thenReturn(Optional.of(order));

        when(rvcRepository.findAllByOrderId(1L))
            .thenReturn(List.of(rvc));

        when(rvcRepository.existsByCargoId(100L))
            .thenReturn(true);

        orderService.delete(1L);

        verify(cargoRepository, never()).delete(any());
    }

    @Test
    void delete_shouldDeleteCargoIfNotUsedElsewhere() {

        Order order = new Order();
        order.setId(1L);

        Cargo cargo = new Cargo();
        cargo.setId(100L);

        RouteVehicleCargo rvc = mock(RouteVehicleCargo.class);
        when(rvc.getCargo()).thenReturn(cargo);

        when(orderRepository.findById(1L))
            .thenReturn(Optional.of(order));

        when(rvcRepository.findAllByOrderId(1L))
            .thenReturn(List.of(rvc));

        when(rvcRepository.existsByCargoId(100L))
            .thenReturn(false);

        orderService.delete(1L);

        verify(cargoRepository).delete(cargo);
    }

    // ---------------- NATIVE QUERY (Object[]) FULL COVERAGE ----------------

    @Test
    void native_shouldHandleNullValues() {

        Object[] row = new Object[]{
            1L, "NEW",
            null, null,
            null, null, null,
            null, null
        };

        when(orderRepository.findOrdersFlat("CargoA"))
            .thenReturn(List.<Object[]>of(row));

        List<OrderResponseDto> result =
            orderService.getOrdersByCargoNative("CargoA");

        assertEquals(1, result.size());
    }

    @Test
    void native_shouldMapMultipleRowsSameOrder() {

        Object[] row1 = new Object[]{
            1L, "NEW", 10L, "C1", 1L, "A", "B", 1L, "V1"
        };

        Object[] row2 = new Object[]{
            1L, "NEW", 20L, "C2", 2L, "C", "D", 2L, "V2"
        };

        when(orderRepository.findOrdersFlat("CargoA"))
            .thenReturn(List.of(row1, row2));

        List<OrderResponseDto> result =
            orderService.getOrdersByCargoNative("CargoA");

        assertEquals(1, result.size());
        assertEquals(2,
            result.get(0).getRouteVehicleCargoList().size());
    }

    // ---------------- CACHE FULL COVERAGE ----------------

    @Test
    void cache_shouldHitAndMiss() {

        when(orderRepository.findOrdersByCargoName("TV"))
            .thenReturn(List.of(new Order()));

        // MISS
        orderService.getOrdersByCargoCached("TV");

        // HIT
        orderService.getOrdersByCargoCached("TV");

        verify(orderRepository, times(1))
            .findOrdersByCargoName("TV");
    }

    // ---------------- PAGINATION ----------------

    @Test
    void getAll_shouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(List.of(new Order())));

        Page<OrderResponseDto> result =
            orderService.getAll(pageable);

        assertEquals(1, result.getContent().size());
    }

    // ---------------- OPTIMIZED ----------------

    @Test
    void getAllOptimized_shouldWork() {

        when(orderRepository.findAllWithRelations())
            .thenReturn(List.of(new Order()));

        List<OrderResponseDto> result =
            orderService.getAllOptimized();

        assertEquals(1, result.size());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getById_shouldThrow() {

        when(orderRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.getById(1L));
    }

    @Test
    void getOrdersByCargo_shouldReturnMappedList() {

        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findOrdersByCargoName("TV"))
            .thenReturn(List.of(order));

        List<OrderResponseDto> result =
            orderService.getOrdersByCargo("TV");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(orderRepository).findOrdersByCargoName("TV");
    }

    @Test
    void getById_shouldReturnDto() {

        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L))
            .thenReturn(Optional.of(order));

        OrderResponseDto result = orderService.getById(1L);

        assertNotNull(result);
    }
}
