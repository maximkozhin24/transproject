package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.VehicleDto;
import com.logistics.logisticsapp.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    // GET с PathVariable
    @GetMapping("/{id}")
    public VehicleDto getById(@PathVariable Long id) {
        return service.getVehicleById(id);
    }

    // GET с RequestParam
    @GetMapping
    public List<VehicleDto> getByModel(
            @RequestParam(required = false) String model
    ) {
        if (model == null) {
            return service.getAllVehicles();
        }
        return service.getVehiclesByModel(model);
    }
}

