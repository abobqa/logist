package org.logistservice.logist.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.logistservice.logist.client.model.Client;
import org.logistservice.logist.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30, name = "order_number")
    private String orderNumber;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "planned_pickup_date")
    private LocalDate plannedPickupDate;
    
    @Column(name = "planned_delivery_date")
    private LocalDate plannedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;
    
    @Size(max = 100)
    @Column(name = "origin_city", length = 100)
    private String originCity;
    
    @Size(max = 255)
    @Column(name = "origin_address", length = 255)
    private String originAddress;
    
    @Size(max = 100)
    @Column(name = "destination_city", length = 100)
    private String destinationCity;
    
    @Size(max = 255)
    @Column(name = "destination_address", length = 255)
    private String destinationAddress;
    
    @Size(max = 500)
    @Column(name = "cargo_description", length = 500)
    private String cargoDescription;
    
    @Positive
    @Column(name = "cargo_weight")
    private Double cargoWeight;
    
    @Positive
    @Column(name = "cargo_volume")
    private Double cargoVolume;
    
    @Positive
    @Column(precision = 19, scale = 2)
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderAssignment> assignments = new HashSet<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderStatusHistory> statusHistory = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

