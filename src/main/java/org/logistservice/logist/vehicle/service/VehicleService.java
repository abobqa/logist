package org.logistservice.logist.vehicle.service;

import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.enums.VehicleSortField;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.logistservice.logist.vehicle.model.dto.VehicleCreateUpdateRequest;
import org.logistservice.logist.vehicle.model.dto.VehicleDto;

import java.util.List;

public interface VehicleService {
    List<VehicleDto> getAll(String typeFilter, VehicleStatus statusFilter, VehicleSortField sortField, SortDirection sortDirection);
    VehicleDto getById(Long id);
    VehicleDto create(VehicleCreateUpdateRequest request);
    VehicleDto update(Long id, VehicleCreateUpdateRequest request);
    void delete(Long id);
}


