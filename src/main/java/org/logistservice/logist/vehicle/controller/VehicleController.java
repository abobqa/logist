package org.logistservice.logist.vehicle.controller;

import jakarta.validation.Valid;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.enums.VehicleSortField;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.logistservice.logist.vehicle.model.dto.VehicleCreateUpdateRequest;
import org.logistservice.logist.vehicle.model.dto.VehicleDto;
import org.logistservice.logist.vehicle.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<List<VehicleDto>> getAllVehicles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) VehicleSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection) {
        return ResponseEntity.ok(vehicleService.getAll(type, status, sortField, sortDirection));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<VehicleDto> createVehicle(@Valid @RequestBody VehicleCreateUpdateRequest request) {
        VehicleDto created = vehicleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleCreateUpdateRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


