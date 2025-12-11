package org.logistservice.logist.order.repository;

import org.logistservice.logist.order.model.OrderAssignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAssignmentRepository extends JpaRepository<OrderAssignment, Long> {
    @EntityGraph(attributePaths = {"vehicle", "order", "driver"})
    @Query("SELECT a FROM OrderAssignment a")
    List<OrderAssignment> findAllWithRelations();
    
    @EntityGraph(attributePaths = {"vehicle", "order"})
    List<OrderAssignment> findByDriverId(Long driverId);
    
    List<OrderAssignment> findByOrderId(Long orderId);
}





