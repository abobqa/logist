package org.logistservice.logist.driver.service;

import org.logistservice.logist.common.enums.DriverSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.driver.model.dto.DriverCreateUpdateRequest;
import org.logistservice.logist.driver.model.dto.DriverDto;

import java.util.List;

public interface DriverService {
    List<DriverDto> getAll(String nameFilter, Boolean active, DriverSortField sortField, SortDirection sortDirection);
    DriverDto getById(Long id);
    DriverDto create(DriverCreateUpdateRequest request);
    DriverDto update(Long id, DriverCreateUpdateRequest request);
    void delete(Long id);
}


