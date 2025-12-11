package org.logistservice.logist.driver.controller;

import jakarta.validation.Valid;
import org.logistservice.logist.common.enums.DriverSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.driver.model.dto.DriverCreateUpdateRequest;
import org.logistservice.logist.driver.model.dto.DriverDto;
import org.logistservice.logist.driver.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    
    private final DriverService driverService;
    
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<List<DriverDto>> getAllDrivers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) DriverSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection) {
        return ResponseEntity.ok(driverService.getAll(name, active, sortField, sortDirection));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<DriverDto> createDriver(@Valid @RequestBody DriverCreateUpdateRequest request) {
        DriverDto created = driverService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<DriverDto> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverCreateUpdateRequest request) {
        return ResponseEntity.ok(driverService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


