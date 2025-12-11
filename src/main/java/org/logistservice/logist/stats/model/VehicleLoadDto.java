package org.logistservice.logist.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleLoadDto {
    private Long vehicleId;
    private String registrationNumber;
    private long ordersCount;
}





