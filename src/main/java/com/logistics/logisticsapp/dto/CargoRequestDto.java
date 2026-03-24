package com.logistics.logisticsapp.dto;

public class CargoRequestDto {

        private String name;
        private double weight;

        // getters & setters

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
}