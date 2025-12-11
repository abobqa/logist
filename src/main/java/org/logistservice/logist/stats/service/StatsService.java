package org.logistservice.logist.stats.service;

import org.logistservice.logist.stats.model.OrderStatusCountDto;
import org.logistservice.logist.stats.model.TopClientDto;
import org.logistservice.logist.stats.model.VehicleLoadDto;

import java.time.LocalDate;
import java.util.List;

public interface StatsService {
    List<OrderStatusCountDto> getOrderStatusCounts(LocalDate fromDate, LocalDate toDate);
    List<TopClientDto> getTopClients(LocalDate fromDate, LocalDate toDate, int limit);
    List<VehicleLoadDto> getVehicleLoad(LocalDate fromDate, LocalDate toDate);
}

