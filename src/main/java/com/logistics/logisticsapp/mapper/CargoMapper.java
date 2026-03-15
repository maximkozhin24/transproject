package com.logistics.logisticsapp.mapper;

import org.springframework.stereotype.Component;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.dto.CargoDto;

@Component
public class CargoMapper {

    public CargoDto toDTO(Cargo cargo) {

        CargoDto dto = new CargoDto();

        dto.setId(cargo.getId());
        dto.setName(cargo.getName());
        dto.setWeight(cargo.getWeight());
        dto.setOrderId(cargo.getOrder().getId());

        return dto;
    }

}
