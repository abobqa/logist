package org.logistservice.logist.user.repository;

import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}





