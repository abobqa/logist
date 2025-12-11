package org.logistservice.logist.order.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAssignmentCreateUpdateRequest {
    @NotNull
    private Long vehicleId;
    
    @NotNull
    private Long driverId;
    
    private LocalDateTime plannedStart;
    
    private LocalDateTime plannedEnd;
    
    private LocalDateTime actualStart;
    
    private LocalDateTime actualEnd;
}




