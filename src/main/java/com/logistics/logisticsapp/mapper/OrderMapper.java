package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.dto.RouteVehicleCargoResponseDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.Order;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static Order toEntity(OrderRequestDto dto) {

        Order order = new Order();

        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        return order;
    }

    public static OrderResponseDto toDtoWithRelations(Order order) {

        OrderResponseDto dto = new OrderResponseDto();

        dto.setId(order.getId());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());

        // CLIENT
        if (order.getClient() != null) {
            dto.setClient(ClientMapper.toDto(order.getClient()));
        }

        // ROUTES
        List<RouteResponseDto> routes = order.getRouteVehicleCargoList()
            .stream()
            .map(rvc -> {
                RouteResponseDto r = new RouteResponseDto();

                if (rvc.getRoute() != null) {
                    r.setId(rvc.getRoute().getId());
                    r.setStartLocation(rvc.getRoute().getStartLocation());
                    r.setEndLocation(rvc.getRoute().getEndLocation());
                    r.setDistance(rvc.getRoute().getDistance());
                }

                return r;
            })
            .toList();

        // CARGOS
        List<CargoResponseDto> cargos = order.getRouteVehicleCargoList()
            .stream()
            .map(rvc -> {
                CargoResponseDto c = new CargoResponseDto();

                if (rvc.getCargo() != null) {
                    c.setId(rvc.getCargo().getId());
                    c.setName(rvc.getCargo().getName());
                    c.setWeight(rvc.getCargo().getWeight());
                }

                return c;
            })
            .toList();

        // VEHICLES
        List<VehicleResponseDto> vehicles = order.getRouteVehicleCargoList()
            .stream()
            .map(rvc -> {

                if (rvc.getVehicle() == null) {
                    return null;
                }

                VehicleResponseDto v = new VehicleResponseDto();

                v.setId(rvc.getVehicle().getId());
                v.setModel(rvc.getVehicle().getModel());
                v.setPlateNumber(rvc.getVehicle().getPlateNumber());
                v.setCapacity(rvc.getVehicle().getCapacity());

                return v;
            })
            .toList();

        // ROUTE VEHICLE CARGO LIST
        List<RouteVehicleCargoResponseDto> rvcList = order.getRouteVehicleCargoList()
            .stream()
            .map(rvc -> {

                RouteVehicleCargoResponseDto rvcDto =
                    new RouteVehicleCargoResponseDto();

                // ROUTE
                if (rvc.getRoute() != null) {

                    RouteResponseDto routeDto = new RouteResponseDto();

                    routeDto.setId(rvc.getRoute().getId());
                    routeDto.setStartLocation(
                        rvc.getRoute().getStartLocation()
                    );
                    routeDto.setEndLocation(
                        rvc.getRoute().getEndLocation()
                    );
                    routeDto.setDistance(
                        rvc.getRoute().getDistance()
                    );

                    rvcDto.setRoute(routeDto);
                }

                // CARGO
                if (rvc.getCargo() != null) {

                    CargoResponseDto cargoDto =
                        new CargoResponseDto();

                    cargoDto.setId(rvc.getCargo().getId());
                    cargoDto.setName(rvc.getCargo().getName());
                    cargoDto.setWeight(rvc.getCargo().getWeight());

                    rvcDto.setCargo(cargoDto);
                }

                // VEHICLE
                if (rvc.getVehicle() != null) {

                    VehicleResponseDto vehicleDto =
                        new VehicleResponseDto();

                    vehicleDto.setId(rvc.getVehicle().getId());
                    vehicleDto.setModel(rvc.getVehicle().getModel());
                    vehicleDto.setPlateNumber(
                        rvc.getVehicle().getPlateNumber()
                    );
                    vehicleDto.setCapacity(
                        rvc.getVehicle().getCapacity()
                    );

                    rvcDto.setVehicle(vehicleDto);
                }

                return rvcDto;
            })
            .toList();

        dto.setRoutes(routes);
        dto.setCargos(cargos);
        dto.setVehicles(vehicles);

        return dto;
    }
}