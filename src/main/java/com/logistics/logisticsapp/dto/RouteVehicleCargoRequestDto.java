package com.logistics.logisticsapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RouteVehicleCargoRequestDto {
    @Schema(example = "1")
    private Long routeId;
    @Schema(example = "1")
    private Long cargoId;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getCargoId() {
        return cargoId;
    }

    public void setCargoId(Long cargoId) {
        this.cargoId = cargoId;
    }
}