package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // 🔥 связь с клиентом
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // 🔥 главная связь — через связующую таблицу
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteVehicleCargo> routeVehicleCargoList = new ArrayList<>();

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Client getClient() {
        return client;
    }

    public List<RouteVehicleCargo> getRouteVehicleCargoList() {
        return routeVehicleCargoList;
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

    public void setClient(Client client) {
        this.client = client;
    }

    public void setRouteVehicleCargoList(List<RouteVehicleCargo> list) {
        this.routeVehicleCargoList = list;
    }
}