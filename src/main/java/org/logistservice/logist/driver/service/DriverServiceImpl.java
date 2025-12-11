package org.logistservice.logist.driver.service;

import org.logistservice.logist.common.enums.DriverSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.driver.model.Driver;
import org.logistservice.logist.driver.model.dto.DriverCreateUpdateRequest;
import org.logistservice.logist.driver.model.dto.DriverDto;
import org.logistservice.logist.driver.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {
    
    private final DriverRepository driverRepository;
    
    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }
    
    @Override
    public List<DriverDto> getAll(String nameFilter, Boolean active, DriverSortField sortField, SortDirection sortDirection) {
        List<Driver> drivers = driverRepository.findAll();
        
        List<DriverDto> result = drivers.stream()
                .filter(driver -> {
                    boolean nameMatch = !StringUtils.hasText(nameFilter) ||
                            (driver.getFullName() != null && 
                             driver.getFullName().toLowerCase().contains(nameFilter.toLowerCase()));
                    boolean activeMatch = active == null || driver.getActive().equals(active);
                    return nameMatch && activeMatch;
                })
                .map(this::toDto)
                .collect(Collectors.toList());
        
        // Применяем сортировку
        if (sortField != null) {
            Comparator<DriverDto> comparator = getDriverComparator(sortField);
            if (sortDirection == SortDirection.DESC) {
                comparator = comparator.reversed();
            }
            result = result.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    private Comparator<DriverDto> getDriverComparator(DriverSortField sortField) {
        return switch (sortField) {
            case FULL_NAME -> Comparator.comparing(
                    driver -> driver.getFullName() != null ? driver.getFullName() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case EXPERIENCE_YEARS -> Comparator.comparing(
                    DriverDto::getExperienceYears,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case LICENSE_NUMBER -> Comparator.comparing(
                    driver -> driver.getDrivingLicense() != null ? driver.getDrivingLicense() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case CREATED_AT -> Comparator.comparing(
                    driver -> driver.getId() != null ? driver.getId() : 0L,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }
    
    @Override
    public DriverDto getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver not found with id: " + id));
        return toDto(driver);
    }
    
    @Override
    public DriverDto create(DriverCreateUpdateRequest request) {
        Driver driver = Driver.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .drivingLicense(request.getDrivingLicense())
                .experienceYears(request.getExperienceYears())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        
        Driver saved = driverRepository.save(driver);
        return toDto(saved);
    }
    
    @Override
    public DriverDto update(Long id, DriverCreateUpdateRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver not found with id: " + id));
        
        updateEntityFromRequest(driver, request);
        Driver updated = driverRepository.save(driver);
        return toDto(updated);
    }
    
    @Override
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new NotFoundException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }
    
    private DriverDto toDto(Driver entity) {
        return DriverDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .phone(entity.getPhone())
                .drivingLicense(entity.getDrivingLicense())
                .experienceYears(entity.getExperienceYears())
                .active(entity.getActive())
                .build();
    }
    
    private void updateEntityFromRequest(Driver entity, DriverCreateUpdateRequest request) {
        entity.setFullName(request.getFullName());
        entity.setPhone(request.getPhone());
        entity.setDrivingLicense(request.getDrivingLicense());
        entity.setExperienceYears(request.getExperienceYears());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}


