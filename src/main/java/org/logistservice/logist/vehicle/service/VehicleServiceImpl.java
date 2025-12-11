package org.logistservice.logist.vehicle.service;

import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.enums.VehicleSortField;
import org.logistservice.logist.common.exception.BadRequestException;
import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.vehicle.model.Vehicle;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.logistservice.logist.vehicle.model.dto.VehicleCreateUpdateRequest;
import org.logistservice.logist.vehicle.model.dto.VehicleDto;
import org.logistservice.logist.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
    @Override
    public List<VehicleDto> getAll(String typeFilter, VehicleStatus statusFilter, VehicleSortField sortField, SortDirection sortDirection) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        
        List<VehicleDto> result = vehicles.stream()
                .filter(vehicle -> {
                    boolean typeMatch = !StringUtils.hasText(typeFilter) ||
                            (vehicle.getType() != null && 
                             vehicle.getType().toLowerCase().contains(typeFilter.toLowerCase()));
                    boolean statusMatch = statusFilter == null || vehicle.getStatus() == statusFilter;
                    return typeMatch && statusMatch;
                })
                .map(this::toDto)
                .collect(Collectors.toList());
        
        // Применяем сортировку
        if (sortField != null) {
            Comparator<VehicleDto> comparator = getVehicleComparator(sortField);
            if (sortDirection == SortDirection.DESC) {
                comparator = comparator.reversed();
            }
            result = result.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    private Comparator<VehicleDto> getVehicleComparator(VehicleSortField sortField) {
        return switch (sortField) {
            case REGISTRATION_NUMBER -> Comparator.comparing(
                    vehicle -> vehicle.getRegistrationNumber() != null ? vehicle.getRegistrationNumber() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case TYPE -> Comparator.comparing(
                    vehicle -> vehicle.getType() != null ? vehicle.getType() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case STATUS -> Comparator.comparing(
                    VehicleDto::getStatus,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case CAPACITY_WEIGHT -> Comparator.comparing(
                    VehicleDto::getCapacityWeight,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case CREATED_AT -> Comparator.comparing(
                    VehicleDto::getCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }
    
    @Override
    public VehicleDto getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        return toDto(vehicle);
    }
    
    @Override
    public VehicleDto create(VehicleCreateUpdateRequest request) {
        if (vehicleRepository.existsByRegistrationNumberIgnoreCase(request.getRegistrationNumber())) {
            throw new BadRequestException("Автомобиль с данным номером уже существует");
        }
        
        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(request.getRegistrationNumber())
                .type(request.getType())
                .capacityWeight(request.getCapacityWeight())
                .capacityVolume(request.getCapacityVolume())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .build();
        
        Vehicle saved = vehicleRepository.save(vehicle);
        return toDto(saved);
    }
    
    @Override
    public VehicleDto update(Long id, VehicleCreateUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Автомобиль не был найден: " + id));

        if (!vehicle.getRegistrationNumber().equalsIgnoreCase(request.getRegistrationNumber()) &&
            vehicleRepository.existsByRegistrationNumberIgnoreCase(request.getRegistrationNumber())) {
            throw new BadRequestException("Автомобиль с данным номером уже существует");
        }
        
        updateEntityFromRequest(vehicle, request);
        Vehicle updated = vehicleRepository.save(vehicle);
        return toDto(updated);
    }
    
    @Override
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new NotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }
    
    private VehicleDto toDto(Vehicle entity) {
        return VehicleDto.builder()
                .id(entity.getId())
                .registrationNumber(entity.getRegistrationNumber())
                .type(entity.getType())
                .capacityWeight(entity.getCapacityWeight())
                .capacityVolume(entity.getCapacityVolume())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    private void updateEntityFromRequest(Vehicle entity, VehicleCreateUpdateRequest request) {
        entity.setRegistrationNumber(request.getRegistrationNumber());
        entity.setType(request.getType());
        entity.setCapacityWeight(request.getCapacityWeight());
        entity.setCapacityVolume(request.getCapacityVolume());
        entity.setStatus(request.getStatus());
    }
}


