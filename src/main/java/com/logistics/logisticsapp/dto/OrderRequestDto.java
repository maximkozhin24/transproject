package com.logistics.logisticsapp.dto;
import com.logistics.logisticsapp.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class OrderRequestDto {
    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    @Schema(description = "Цена заказа", example = "100.5")
    private Double price;
    @NotNull(message = "Status must not be null")
    @Schema(description = "Статус заказа")
    private OrderStatus status;
    @NotNull(message = "ClientId must not be null")
    @Positive(message = "ClientId must be positive")
    @Schema(description = "ID клиента", example = "1")
    private Long clientId;

    private List<RouteVehicleCargoRequestDto> items;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<RouteVehicleCargoRequestDto> getItems() {
        return items;
    }

    public void setItems(List<RouteVehicleCargoRequestDto> items) {
        this.items = items;
    }
}