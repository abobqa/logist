package org.logistservice.logist.client.repository;

import org.logistservice.logist.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Custom query methods to be added
}




