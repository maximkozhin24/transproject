package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String startLocation;
    private String endLocation;
    private double distance;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToMany
    @JoinTable(
        name = "route_vehicle",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private List<Vehicle> vehicles;

    @ManyToMany
    @JoinTable(
        name = "route_cargo",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "cargo_id")
    )
    private List<Cargo> cargos;

    public Route() {
    }

    // -------- GETTERS --------

    public Long getId() {
        return id;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public double getDistance() {
        return distance;
    }

    public Order getOrder() {
        return order;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    // -------- SETTERS --------

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }
}
