package com.logistics.logisticsapp.dto;

public class RouteVehicleCargoResponseDto {

    private CargoResponseDto cargo;
    private RouteResponseDto route;
    private VehicleResponseDto vehicle;

    public CargoResponseDto getCargo() {
        return cargo;
    }

    public void setCargo(CargoResponseDto cargo) {
        this.cargo = cargo;
    }

    public RouteResponseDto getRoute() {
        return route;
    }

    public void setRoute(RouteResponseDto route) {
        this.route = route;
    }

    public VehicleResponseDto getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleResponseDto vehicle) {
        this.vehicle = vehicle;
    }
}