package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "route_vehicle_cargo")
public class RouteVehicleCargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 ДОБАВИЛИ ORDER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 🔗 Route
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // 🔗 Vehicle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    // 🔗 Cargo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo cargo;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Route getRoute() {
        return route;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}