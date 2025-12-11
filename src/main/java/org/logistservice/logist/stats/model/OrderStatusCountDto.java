package org.logistservice.logist.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.logistservice.logist.order.model.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusCountDto {
    private OrderStatus status;
    private long count;
}





