package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.*;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.repository.RouteRepository;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteVehicleCargoRepository rvcRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RouteService routeService;

    private Route route1;

    @BeforeEach
    void setup() {
        route1 = new Route();
        route1.setId(1L);
        route1.setStartLocation("A");
        route1.setEndLocation("B");
        route1.setDistance(100);
    }

    @Test
    void create_shouldReturnDto() {

        RouteRequestDto dto = new RouteRequestDto();
        dto.setStartLocation("A");
        dto.setEndLocation("B");
        dto.setDistance(100);

        when(routeRepository.save(any(Route.class))).thenReturn(route1);

        RouteResponseDto result = routeService.create(dto);

        assertNotNull(result);
        assertEquals("A", result.getStartLocation());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void getAll_shouldReturnList() {

        when(routeRepository.findAll()).thenReturn(List.of(route1));

        List<RouteResponseDto> result = routeService.getAll();

        assertEquals(1, result.size());
        verify(routeRepository).findAll();
    }

    @Test
    void getById_shouldReturnRoute() {

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route1));

        RouteResponseDto result = routeService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void update_shouldUpdateRoute() {

        RouteRequestDto dto = new RouteRequestDto();
        dto.setStartLocation("X");
        dto.setEndLocation("Y");
        dto.setDistance(200);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route1));
        when(routeRepository.save(any(Route.class))).thenReturn(route1);

        RouteResponseDto result = routeService.update(1L, dto);

        assertEquals("X", result.getStartLocation());
        assertEquals("Y", result.getEndLocation());

        verify(routeRepository).save(route1);
    }

    @Test
    void delete_shouldCallRepository() {

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route1));
        when(rvcRepository.findAllByRouteId(1L)).thenReturn(List.of());

        routeService.delete(1L);

        verify(routeRepository).delete(route1);
    }
    @Test
    void delete_shouldDeleteEverything() {

        Route routte = new Route();
        routte.setId(1L);

        Order order = new Order();
        order.setId(10L);

        Cargo cargo = new Cargo();
        cargo.setId(100L);

        RouteVehicleCargo rvc = new RouteVehicleCargo();
        rvc.setOrder(order);
        rvc.setCargo(cargo);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(routte));
        when(rvcRepository.findAllByRouteId(1L)).thenReturn(List.of(rvc));
        when(rvcRepository.findAllByOrderId(10L)).thenReturn(List.of(rvc));
        when(rvcRepository.existsByCargoId(100L)).thenReturn(false);

        routeService.delete(1L);

        verify(cargoRepository).delete(cargo);
        verify(orderRepository).delete(order);
        verify(routeRepository).delete(routte);
        verify(rvcRepository).deleteAll(anyList());
    }

    @Test
    void delete_shouldNotDeleteCargo_ifUsedElsewhere() {

        Route route = new Route();
        route.setId(1L);

        Order order = new Order();
        order.setId(10L);

        Cargo cargo = new Cargo();
        cargo.setId(100L);

        RouteVehicleCargo rvc = new RouteVehicleCargo();
        rvc.setOrder(order);
        rvc.setCargo(cargo);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(rvcRepository.findAllByRouteId(1L)).thenReturn(List.of(rvc));
        when(rvcRepository.findAllByOrderId(10L)).thenReturn(List.of(rvc));
        when(rvcRepository.existsByCargoId(100L)).thenReturn(true);

        routeService.delete(1L);

        verify(cargoRepository, never()).delete(cargo); // 💥 важно
        verify(orderRepository).delete(order);
    }

    @Test
    void delete_shouldWork_whenNoRelations() {

        Route routte = new Route();
        routte.setId(1L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(routte));
        when(rvcRepository.findAllByRouteId(1L)).thenReturn(List.of());

        routeService.delete(1L);

        verify(routeRepository).delete(routte);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(cargoRepository);
    }

    @Test
    void delete_shouldThrowException_ifRouteNotFound() {

        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> routeService.delete(1L));
    }
}