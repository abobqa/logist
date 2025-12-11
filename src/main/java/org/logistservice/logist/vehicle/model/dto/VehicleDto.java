package org.logistservice.logist.vehicle.model.dto;

import lombok.*;
import org.logistservice.logist.vehicle.model.VehicleStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private Long id;
    private String registrationNumber;
    private String type;
    private Double capacityWeight;
    private Double capacityVolume;
    private VehicleStatus status;
    private LocalDateTime createdAt;
}




