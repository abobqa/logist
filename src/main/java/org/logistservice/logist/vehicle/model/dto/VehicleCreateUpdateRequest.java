package org.logistservice.logist.vehicle.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.logistservice.logist.vehicle.model.VehicleStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleCreateUpdateRequest {
    @NotBlank
    @Size(max = 20)
    private String registrationNumber;
    
    @Size(max = 50)
    private String type;
    
    @Positive
    private Double capacityWeight;
    
    @Positive
    private Double capacityVolume;
    
    @NotNull
    private VehicleStatus status;
}




