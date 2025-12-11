package org.logistservice.logist.order.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateUpdateRequest {
    @NotNull
    private Long clientId;
    
    @NotNull
    private LocalDate plannedPickupDate;
    
    @NotNull
    private LocalDate plannedDeliveryDate;
    
    @NotBlank
    @Size(max = 100)
    private String originCity;
    
    @Size(max = 255)
    private String originAddress;
    
    @NotBlank
    @Size(max = 100)
    private String destinationCity;
    
    @Size(max = 255)
    private String destinationAddress;
    
    @Size(max = 500)
    private String cargoDescription;
    
    @Positive
    private Double cargoWeight;
    
    @Positive
    private Double cargoVolume;
    
    @Positive
    private BigDecimal price;
    
    private Long managerId;
    
    private Long driverId;
    
    private Long vehicleId;
    
    @Size(max = 500)
    private String description;
}


