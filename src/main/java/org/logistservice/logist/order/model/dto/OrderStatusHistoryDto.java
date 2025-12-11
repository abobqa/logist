package org.logistservice.logist.order.model.dto;

import lombok.*;
import org.logistservice.logist.order.model.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryDto {
    private Long id;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private LocalDateTime changedAt;
    private Long changedByUserId;
    private String changedByUsername;
}




