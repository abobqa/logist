package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.stats.model.OrderStatusCountDto;
import org.logistservice.logist.stats.model.TopClientDto;
import org.logistservice.logist.stats.model.VehicleLoadDto;
import org.logistservice.logist.stats.service.StatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/stats")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class StatsPageController {
    
    private final StatsService statsService;
    
    @GetMapping
    public String stats(Model model) {
        var statusStats = statsService.getOrderStatusCounts(null, null);
        var topClients = statsService.getTopClients(null, null, 5);
        var vehicleLoad = statsService.getVehicleLoad(null, null);
        
        model.addAttribute("statusStats", statusStats);
        model.addAttribute("topClients", topClients);
        model.addAttribute("vehicleLoad", vehicleLoad);
        
        // Создаем JSON строки вручную для JavaScript
        model.addAttribute("statusStatsJson", buildStatusStatsJson(statusStats));
        model.addAttribute("topClientsJson", buildTopClientsJson(topClients));
        model.addAttribute("vehicleLoadJson", buildVehicleLoadJson(vehicleLoad));
        
        return "stats";
    }
    
    private String buildStatusStatsJson(List<OrderStatusCountDto> stats) {
        if (stats == null || stats.isEmpty()) {
            return "[]";
        }
        return stats.stream()
                .map(s -> String.format("{\"status\":\"%s\",\"count\":%d}", s.getStatus().name(), s.getCount()))
                .collect(Collectors.joining(",", "[", "]"));
    }
    
    private String buildTopClientsJson(List<TopClientDto> clients) {
        if (clients == null || clients.isEmpty()) {
            return "[]";
        }
        return clients.stream()
                .map(c -> String.format("{\"clientName\":\"%s\",\"ordersCount\":%d,\"totalPrice\":%s}",
                        escapeJson(c.getClientName()),
                        c.getOrdersCount(),
                        c.getTotalPrice() != null ? c.getTotalPrice().toString() : "null"))
                .collect(Collectors.joining(",", "[", "]"));
    }
    
    private String buildVehicleLoadJson(List<VehicleLoadDto> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            return "[]";
        }
        return vehicles.stream()
                .map(v -> String.format("{\"registrationNumber\":\"%s\",\"ordersCount\":%d}",
                        escapeJson(v.getRegistrationNumber()),
                        v.getOrdersCount()))
                .collect(Collectors.joining(",", "[", "]"));
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}




