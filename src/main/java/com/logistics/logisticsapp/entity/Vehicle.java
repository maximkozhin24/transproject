package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;
    private String model;
    private double capacity;

    @ManyToMany(mappedBy = "vehicles")
    private List<Route> routes;

    public Vehicle() {
    }

    // -------- GETTERS --------

    public Long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getModel() {
        return model;
    }

    public double getCapacity() {
        return capacity;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    // -------- SETTERS --------

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
