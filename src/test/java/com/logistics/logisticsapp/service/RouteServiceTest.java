package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.repository.RouteRepository;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteVehicleCargoRepository rvcRepository;

    @InjectMocks
    private RouteService routeService;

    private Route route;

    @BeforeEach
    void setup() {
        route = new Route();
        route.setId(1L);
        route.setStartLocation("A");
        route.setEndLocation("B");
        route.setDistance(100);
    }

    @Test
    void create_shouldReturnDto() {

        RouteRequestDto dto = new RouteRequestDto();
        dto.setStartLocation("A");
        dto.setEndLocation("B");
        dto.setDistance(100);

        when(routeRepository.save(any(Route.class))).thenReturn(route);

        RouteResponseDto result = routeService.create(dto);

        assertNotNull(result);
        assertEquals("A", result.getStartLocation());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void getAll_shouldReturnList() {

        when(routeRepository.findAll()).thenReturn(List.of(route));

        List<RouteResponseDto> result = routeService.getAll();

        assertEquals(1, result.size());
        verify(routeRepository).findAll();
    }

    @Test
    void getById_shouldReturnRoute() {

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        RouteResponseDto result = routeService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void update_shouldUpdateRoute() {

        RouteRequestDto dto = new RouteRequestDto();
        dto.setStartLocation("X");
        dto.setEndLocation("Y");
        dto.setDistance(200);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenReturn(route);

        RouteResponseDto result = routeService.update(1L, dto);

        assertEquals("X", route.getStartLocation());
        verify(routeRepository).save(route);
    }

    @Test
    void delete_shouldCallRepository() {

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(rvcRepository.findAllByRouteId(1L)).thenReturn(List.of());

        routeService.delete(1L);

        verify(routeRepository).delete(route);
    }
    @Test
    void delete_shouldThrowException() {

        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> routeService.delete(1L));
    }
}