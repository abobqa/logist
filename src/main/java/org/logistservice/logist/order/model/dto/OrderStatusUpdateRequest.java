package org.logistservice.logist.order.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.logistservice.logist.order.model.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequest {
    @NotNull
    private OrderStatus newStatus;
}




