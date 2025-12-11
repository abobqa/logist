package org.logistservice.logist.driver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.logistservice.logist.order.model.OrderAssignment;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Size(max = 30)
    @Column(length = 30)
    private String phone;
    
    @Size(max = 50)
    @Column(name = "license_number", length = 50)
    private String drivingLicense;
    
    @PositiveOrZero
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderAssignment> assignments = new HashSet<>();
}

