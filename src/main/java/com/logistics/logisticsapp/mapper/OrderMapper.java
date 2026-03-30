package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
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

        List<RouteResponseDto> routes = order.getRouteVehicleCargoList()
            .stream()
            .map(rvc -> {
                RouteResponseDto r = new RouteResponseDto();
                r.setId(rvc.getRoute().getId());
                r.setStartLocation(rvc.getRoute().getStartLocation());
                r.setEndLocation(rvc.getRoute().getEndLocation());
                r.setDistance(rvc.getRoute().getDistance());
                return r;
            })
            .toList();

        List<CargoResponseDto> cargos = order.getRouteVehicleCargoList()
            .stream()
            .map(rvc -> {
                CargoResponseDto c = new CargoResponseDto();
                c.setId(rvc.getCargo().getId());
                c.setName(rvc.getCargo().getName());
                c.setWeight(rvc.getCargo().getWeight());
                return c;
            })
            .toList();

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

        dto.setRoutes(routes);
        dto.setCargos(cargos);
        dto.setVehicles(vehicles);

        return dto;
    }
}