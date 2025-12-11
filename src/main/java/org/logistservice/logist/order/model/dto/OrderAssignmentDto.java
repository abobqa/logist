package org.logistservice.logist.order.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAssignmentDto {
    private Long id;
    private Long orderId;
    private Long vehicleId;
    private String vehicleRegistrationNumber;
    private Long driverId;
    private String driverName;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
}




