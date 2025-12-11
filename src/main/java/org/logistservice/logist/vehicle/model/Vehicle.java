package org.logistservice.logist.vehicle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.logistservice.logist.order.model.OrderAssignment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber;
    
    @Size(max = 50)
    @Column(length = 50)
    private String type;
    
    @Positive
    @Column(name = "capacity_weight")
    private Double capacityWeight;
    
    @Positive
    @Column(name = "capacity_volume")
    private Double capacityVolume;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderAssignment> assignments = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

