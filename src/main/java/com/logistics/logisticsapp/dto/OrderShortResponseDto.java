package com.logistics.logisticsapp.dto;

import com.logistics.logisticsapp.entity.OrderStatus;

public class OrderShortResponseDto {

    private Long id;
    private Double price;
    private OrderStatus status;

    public Long getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}