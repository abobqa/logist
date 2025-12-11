package org.logistservice.logist.order.model.dto;

import lombok.*;
import org.logistservice.logist.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Long clientId;
    private String clientName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDate plannedPickupDate;
    private LocalDate plannedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String originCity;
    private String originAddress;
    private String destinationCity;
    private String destinationAddress;
    private String cargoDescription;
    private Double cargoWeight;
    private Double cargoVolume;
    private BigDecimal price;
    private Long managerId;
    private String managerName;
}




