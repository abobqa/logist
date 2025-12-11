package org.logistservice.logist.order.repository;

import org.logistservice.logist.order.model.Order;
import org.logistservice.logist.order.model.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByClientId(Long clientId);
    
    @EntityGraph(attributePaths = {"client", "manager"})
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithClientAndManager();
}

