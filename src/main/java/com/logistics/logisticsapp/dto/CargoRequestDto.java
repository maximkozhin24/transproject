package com.logistics.logisticsapp.dto;

public class CargoRequestDto {

        private String name;
        private double weight;
        private Long orderId;

        public CargoRequestDto() {}

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public double getWeight() {
                return weight;
        }

        public void setWeight(double weight) {
                this.weight = weight;
        }

        public Long getOrderId() {
                return orderId;
        }

        public void setOrderId(Long orderId) {
                this.orderId = orderId;
        }
}