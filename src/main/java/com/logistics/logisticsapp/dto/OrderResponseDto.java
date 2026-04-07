package com.logistics.logisticsapp.dto;

import com.logistics.logisticsapp.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private Long id;
    private double price;
    private OrderStatus status;

    private List<RouteResponseDto> routes;
    private List<CargoResponseDto> cargos;
    private List<VehicleResponseDto> vehicles;
    private List<RouteVehicleCargoResponseDto> routeVehicleCargoList;

    public Long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<RouteResponseDto> getRoutes() {
        return routes;
    }

    public List<CargoResponseDto> getCargos() {
        return cargos;
    }

    public List<VehicleResponseDto> getVehicles() {
        return vehicles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setRoutes(List<RouteResponseDto> routes) {
        this.routes = routes;
    }

    public void setCargos(List<CargoResponseDto> cargos) {
        this.cargos = cargos;
    }

    public void setVehicles(List<VehicleResponseDto> vehicles) {
        this.vehicles = vehicles;
    }
    public List<RouteVehicleCargoResponseDto> getRouteVehicleCargoList() {
        return routeVehicleCargoList;
    }

    public void setRouteVehicleCargoList(List<RouteVehicleCargoResponseDto> routeVehicleCargoList) {
        this.routeVehicleCargoList = routeVehicleCargoList;
    }
}