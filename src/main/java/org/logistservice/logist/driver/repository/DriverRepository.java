package org.logistservice.logist.driver.repository;

import org.logistservice.logist.driver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    long countByActive(Boolean active);
}





