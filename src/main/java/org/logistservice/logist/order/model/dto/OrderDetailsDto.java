package org.logistservice.logist.order.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsDto {
    private OrderDto order;
    private List<OrderAssignmentDto> assignments;
    private List<OrderStatusHistoryDto> statusHistory;
}




