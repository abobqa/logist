package org.logistservice.logist.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopClientDto {
    private Long clientId;
    private String clientName;
    private long ordersCount;
    private BigDecimal totalPrice;
}





