package com.logistics.logisticsapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "phone", unique = true, length = 20, nullable = false)
    private String phone;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public String getName() {
        return name; }
    public void setName(String name) {
        this.name = name; }

    public String getEmail() {
        return email; }
    public void setEmail(String email) {
        this.email = email; }

    public String getPhone() {
        return phone; }
    public void setPhone(String phone) {
        this.phone = phone; }

    public List<Order> getOrders() {
        return orders; }
    public void setOrders(List<Order> orders) {
        this.orders = orders; }
}