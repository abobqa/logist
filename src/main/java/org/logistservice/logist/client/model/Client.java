package org.logistservice.logist.client.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.logistservice.logist.order.model.Order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;
    
    @Size(max = 100)
    @Column(length = 100)
    private String contactPerson;
    
    @Size(max = 30)
    @Column(length = 30)
    private String phone;
    
    @Email
    @Size(max = 100)
    @Column(length = 100)
    private String email;
    
    @Size(max = 20)
    @Column(length = 20)
    private String taxNumber;
    
    @Size(max = 100)
    @Column(length = 100)
    private String city;
    
    @Size(max = 255)
    @Column(length = 255)
    private String address;
    
    @Column(nullable = true)
    @Builder.Default
    private Boolean active = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

