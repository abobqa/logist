package org.logistservice.logist.stats.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.logistservice.logist.common.exception.BadRequestException;
import org.logistservice.logist.stats.model.OrderStatusCountDto;
import org.logistservice.logist.stats.model.TopClientDto;
import org.logistservice.logist.stats.model.VehicleLoadDto;
import org.logistservice.logist.stats.service.StatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Validated
@RequiredArgsConstructor
public class StatsController {
    
    private final StatsService statsService;
    
    @GetMapping("/order-status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<OrderStatusCountDto>> getOrderStatusCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BadRequestException("toDate must be after or equal to fromDate");
        }
        
        return ResponseEntity.ok(statsService.getOrderStatusCounts(fromDate, toDate));
    }
    
    @GetMapping("/top-clients")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<TopClientDto>> getTopClients(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, defaultValue = "5") @Min(1) int limit) {
        
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BadRequestException("toDate must be after or equal to fromDate");
        }
        
        if (limit < 1) {
            throw new BadRequestException("limit must be at least 1");
        }
        
        return ResponseEntity.ok(statsService.getTopClients(fromDate, toDate, limit));
    }
    
    @GetMapping("/vehicle-load")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<VehicleLoadDto>> getVehicleLoad(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BadRequestException("toDate must be after or equal to fromDate");
        }
        
        return ResponseEntity.ok(statsService.getVehicleLoad(fromDate, toDate));
    }
}

