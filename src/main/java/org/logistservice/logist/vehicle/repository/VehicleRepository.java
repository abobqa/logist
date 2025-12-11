package org.logistservice.logist.vehicle.repository;

import org.logistservice.logist.vehicle.model.Vehicle;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
    long countByStatus(VehicleStatus status);
}


