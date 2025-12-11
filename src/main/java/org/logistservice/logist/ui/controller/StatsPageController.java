package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.stats.service.StatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        
        return "stats";
    }
}




