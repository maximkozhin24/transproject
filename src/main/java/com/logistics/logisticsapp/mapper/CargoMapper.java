package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.entity.Cargo;

public class CargoMapper {
private CargoMapper(){
}
    public static Cargo toEntity(CargoRequestDto dto) {
        Cargo cargo = new Cargo();
        cargo.setName(dto.getName());
        cargo.setWeight(dto.getWeight());
        return cargo;
    }

    public static CargoResponseDto toDto(Cargo cargo) {
        CargoResponseDto dto = new CargoResponseDto();
        dto.setId(cargo.getId());
        dto.setName(cargo.getName());
        dto.setWeight(cargo.getWeight());
        return dto;
    }
}