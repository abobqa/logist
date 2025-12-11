package org.logistservice.logist.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private org.logistservice.logist.vehicle.model.Vehicle vehicle;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private org.logistservice.logist.driver.model.Driver driver;
    
    @Column(name = "planned_start")
    private LocalDateTime plannedStart;
    
    @Column(name = "planned_end")
    private LocalDateTime plannedEnd;
    
    @Column(name = "actual_start")
    private LocalDateTime actualStart;
    
    @Column(name = "actual_end")
    private LocalDateTime actualEnd;
}





